package com.whisnuys.qlinikku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    String dokterID, tanggalPilih;
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

        DayScrollDatePicker mPicker = (DayScrollDatePicker) findViewById(R.id.day_date_picker);

        nama.setText(getIntent().getExtras().getString("dokterNama"));
        jk.setText(getIntent().getExtras().getString("dokterJK"));
        spesialis.setText(getIntent().getExtras().getString("dokterSpesialis"));
        noHp.setText(getIntent().getExtras().getString("dokterNoHp"));
        Glide.with(this).load(getIntent().getExtras().getString("dokterGambar"))
                .error(R.drawable.avatarhome).into(gambarDokter);

        DatabaseReference mRefDate = mDatabase.getReference("Dokter").child(dokterID).child("slots");
        mRefDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Date> dates = new ArrayList<>();
                for (DataSnapshot date: snapshot.getChildren()) {
                    Locale.setDefault(new Locale("in", "ID"));
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date datefor = format.parse(date.getKey());
                        dates.add(datefor);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                String day1String = (String) DateFormat.format("dd",dates.get(0));
                String monthNumber1String = (String) DateFormat.format("MM",dates.get(0));
                String year1String = (String) DateFormat.format("yyyy",dates.get(0));
                String day2String = (String) DateFormat.format("dd",dates.get(dates.size()-1));
                String monthNumber2String = (String) DateFormat.format("MM",dates.get(dates.size()-1));
                String year2String = (String) DateFormat.format("yyyy",dates.get(dates.size()-1));
                int day1 = Integer.parseInt(day1String);
                int monthNumber1 = Integer.parseInt(monthNumber1String);
                int year1 = Integer.parseInt(year1String);
                int day2 = Integer.parseInt(day2String);
                int monthNumber2 = Integer.parseInt(monthNumber2String);
                int year2 = Integer.parseInt(year2String);
                Log.e("date0", String.valueOf(dates.get(0)));

                mPicker.setStartDate(day1, monthNumber1, year1);
                mPicker.setEndDate(day2, monthNumber2, year2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mPicker.getSelectedDate(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@Nullable Date date) {
                if(date != null){
                    Locale.setDefault(new Locale("in", "ID"));
                    SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
                    tanggalPilih = formater.format(date);
                    mRef.child("slots").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            slotTimes.clear();
                            docTimings.clear();
                            docReserved.clear();
                            for (DataSnapshot tgl: snapshot.getChildren()) {
                                if (tgl.getKey().equals(tanggalPilih)) {
                                    for (DataSnapshot slot: tgl.getChildren()) {
                                        String stand = slot.getValue(String.class);
                                        slotTimes.add(slot.getKey());
                                        if (stand.equals("Available"))
                                            docTimings.add(slot.getKey());
                                        else if (stand.equals("Reserved"))
                                            docReserved.add(slot.getKey());
                                    }
                                }
                            }
                            Log.e("slotTimes", String.valueOf(slotTimes));
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
                            slotCards(slotTimes,tanggalPilih);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });




        mRef.child("slots").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                SimpleDateFormat formatTgl = new SimpleDateFormat("dd-MM-yyyy");

                    try {
                        for (DataSnapshot slot: snapshot.getChildren()) {
                        Date now = new Date();
                        String nowTime = formatTime.format(now);
                        String nowTgl = formatTgl.format(now);
                        Date nowTimeDate = formatTime.parse(nowTime);
                        Date nowTglDate = formatTgl.parse(nowTgl);
                        String slotTgl = slot.getKey();
                        Log.e("slotTgl",slotTgl);
                        Date slotTglDate = formatTgl.parse(slotTgl);
                            for (DataSnapshot slot2: slot.getChildren()) {
                                String slotTime = slot2.getKey();
                                String slotTimeSub = slotTime.substring(8);
                                Date slotTimeDate = formatTime.parse(slotTimeSub);
                                    if (slotTglDate.before(nowTglDate) || slotTglDate.equals(nowTglDate) && slotTimeDate.before(nowTimeDate)){
                                        mRef.child("slots").child(slotTgl).child(slot2.getKey()).setValue("Booked");
                                    }
                            }
                    }} catch (ParseException e) {
                        throw new RuntimeException(e);
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void slotCards(ArrayList<String> slotTimes, String tanggalPilih) {
        slotCards.setHasFixedSize(true);
        slotCards.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> sectionList = new ArrayList<>();
        sectionList.add("Available");
        itemList.put(sectionList.get(0), slotTimes);
        adapter = new DetailPilihDokterAdapter(this, sectionList, itemList, docTimings, docReserved, dokterID, tanggalPilih);
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
                ref1.child("slots").child(tanggalPilih).child(sItem).setValue("Booked");

                ref1.child("my_app").child(tanggalPilih).child(sItem).child("pasien").setValue(firebaseUser.getUid());
                ref1.child("my_app").child(tanggalPilih).child(sItem).child("remarks").setValue(msg);
                ref1.child("my_app").child(tanggalPilih).child(sItem).child("slot").setValue(sItem);

                ref2.child(tanggalPilih + sItem).child("name").setValue(dr_name);
                ref2.child(tanggalPilih + sItem).child("time").setValue(sItem);
                ref2.child(tanggalPilih + sItem).child("tanggal").setValue(tanggalPilih);
                ref2.child(tanggalPilih + sItem).child("gambar").setValue(dr_gambar);
                ref2.child(tanggalPilih + sItem).child("remarks").setValue(msg);

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
                ref1.child("slots").child(tanggalPilih).child(sItem).setValue("Available");
                startActivity(intent);
                finish();
            }
        }, 5000*60);
    }

    public void detailReservasiBackToHome(View view) {
        startActivity(new Intent(this, PasienHome.class));
    }

}