package com.whisnuys.qlinikku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class DetailPilihDokter extends AppCompatActivity {

    ArrayList<String> slotTimes = new ArrayList<>();
    ArrayList<String> docTimings = new ArrayList<>();
    ArrayList<String> docReserved = new ArrayList<>();

    Timer timer;

    HashMap<String, ArrayList<String>> itemList = new HashMap<>();
    DetailPilihDokterAdapter adapter;

    RecyclerView slotCards;
    ShapeableImageView gambarDokter;
    TextView nama, jk, spesialis, noHp;
    Button btn;
    String dokterID;
    EditText remarks;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ValueEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pilih_dokter);

        dokterID = getIntent().getExtras().getString("dokterID");
        slotCards = findViewById(R.id.dr_app_place2);
        btn = findViewById(R.id.dr_app_btn);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Dokter").child(dokterID);

        gambarDokter = findViewById(R.id.gambarDokterPilihan);
        nama = findViewById(R.id.namaDokterPilihan);
        jk = findViewById(R.id.jkDokterPilihan);
        spesialis = findViewById(R.id.spesialisDokterPilihan);
        noHp = findViewById(R.id.noHpDokterPilihan);
        remarks = findViewById(R.id.remarks);

        nama.setText(getIntent().getExtras().getString("dokterNama"));
        jk.setText(getIntent().getExtras().getString("dokterJK"));
        spesialis.setText(getIntent().getExtras().getString("dokterSpesialis"));
        noHp.setText(getIntent().getExtras().getString("dokterNoHp"));
        Glide.with(this).load(getIntent().getExtras().getString("dokterGambar"))
                .error(R.drawable.avatarhome).into(gambarDokter);

        mRef.child("slots").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot slot: snapshot.getChildren()) {
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");

                    try {
                        Date now = new Date();
                        String nowTime = format.format(now);
                        Date nowTimeDate = format.parse(nowTime);
                        String slotTime = slot.getKey();
                        Date slotTimeDate = format.parse(slotTime);
                        if (slotTimeDate.after(nowTimeDate))
                            mRef.child("slots").child(slot.getKey()).setValue("Available");
                        else if (slotTimeDate.before(nowTimeDate))
                            mRef.child("slots").child(slot.getKey()).setValue("Booked");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                slotTimes.clear();
                docTimings.clear();
                docReserved.clear();
                for (DataSnapshot slot: snapshot.getChildren()) {
                    String stand = slot.getValue(String.class);
                    slotTimes.add(slot.getKey());
                    if (stand.equals("Available"))
                        docTimings.add(slot.getKey());
                    else if (stand.equals("Reserved"))
                        docReserved.add(slot.getKey());
                }
                Collections.sort(slotTimes, new Comparator<String>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public int compare(String o1, String o2) {
                        try {
                            return new SimpleDateFormat("hh:mm a").parse(o1).compareTo(new SimpleDateFormat("hh:mm a").parse(o2));
                        } catch (ParseException e) {
                            return 0;
                        }
                    }
                });
                slotCards(slotTimes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mRef.child("slots").addValueEventListener(mListener);
    }

    private void slotCards(ArrayList<String> slotTimes) {
        slotCards.setHasFixedSize(true);
        slotCards.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> sectionList = new ArrayList<>();
        sectionList.add("Jadwal hari ini");
        itemList.put(sectionList.get(0), slotTimes);
        adapter = new DetailPilihDokterAdapter(this, sectionList, itemList, docTimings, docReserved, dokterID);
        GridLayoutManager manager = new GridLayoutManager(this, 3);

        slotCards.setLayoutManager(manager);
        adapter.setLayoutManager(manager);
        slotCards.setAdapter(adapter);
        dokterID = getIntent().getExtras().getString("dokterID");
        String msg = remarks.getText().toString();
        String dr_name = getIntent().getExtras().getString("dokterNama");
        String dr_gambar = getIntent().getExtras().getString("dokterGambar");
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Dokter").child(dokterID);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("active_app");
        btn.setOnClickListener(v -> {
            String sItem = adapter.getSelected();


            if (sItem != null) {
                ref1.child("slots").child(sItem).setValue("Booked");

                ref1.child("my_app").child(sItem).child("pasien").setValue(firebaseUser.getUid());
                ref1.child("my_app").child(sItem).child("remarks").setValue(msg);
                ref1.child("my_app").child(sItem).child("slot").setValue(sItem);

                ref2.child(dr_name + sItem).child("name").setValue(dr_name);
                ref2.child(dr_name + sItem).child("time").setValue(sItem);
                ref2.child(dr_name + sItem).child("gambar").setValue(dr_gambar);
                ref2.child(dr_name + sItem).child("remarks").setValue(msg);

                timer.cancel();
                Toast.makeText(this, "Reservasi Berhasil", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PasienHome.class);
                startActivity(intent);
                finish();

            }
            else
                Toast.makeText(this, "Mohon pilih waktu reservasi", Toast.LENGTH_SHORT).show();
        });
        String sItem = adapter.getSelected();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), PasienHome.class);
                intent.putExtra("pasienID", firebaseUser.getUid());
                ref1.child("slots").child(sItem).setValue("Available");
                startActivity(intent);
                finish();
            }
        }, 5000*60);
    }

    public void detailReservasiBackToHome(View view) {
        startActivity(new Intent(this, PasienHome.class));
    }

}