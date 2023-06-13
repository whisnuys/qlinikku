package com.whisnuys.qlinikku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whisnuys.qlinikku.Models.Dokter;
import com.whisnuys.qlinikku.Models.Pasien;

import java.util.ArrayList;

public class ListDataPasien extends AppCompatActivity {
    private RecyclerView recyclerView;
    ListDataPasienAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference reference;
    private ArrayList<Pasien> dataPasien;
    private EditText searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data_pasien);
        recyclerView = findViewById(R.id.datalistPasien);

        GetData("");

        searchView = findViewById(R.id.etSearchPasien);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    GetData(s.toString());
                } else {
                    adapter.getFilter().filter(s);
                }
            }
        });

        MyRecycleView();
    }

    private void GetData(String data){
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataPasien = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String role = snapshot.child("role").getValue().toString();
                    if(role.equals("pasien")){
                        String nama = snapshot.child("namaLengkap").getValue().toString();
                        String nik = snapshot.child("nik").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        String password = snapshot.child("password").getValue().toString();
                        String alamat = snapshot.child("alamat").getValue().toString();
                        String noTelp = snapshot.child("noHp").getValue().toString();
                        String jenisKelamin = snapshot.child("jenisKelamin").getValue().toString();
                        String tanggalLahir = snapshot.child("tanggalLahir").getValue().toString();
                        String type = snapshot.child("role").getValue().toString();
                        String gambar = snapshot.child("gambar").getValue().toString();
                        String uid = snapshot.child("id").getValue().toString();

                        dataPasien.add(new Pasien(nama, nik, type, jenisKelamin, tanggalLahir, alamat, noTelp, email, password, gambar, uid));
                    }
                }


                adapter = new ListDataPasienAdapter(dataPasien, ListDataPasien.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListDataPasien.this, "Data gagal dimuat", Toast.LENGTH_SHORT).show();
                Log.e("MyListActivity", error.getDetails() + " " + error.getMessage());
            }
        });
    }



    private void MyRecycleView(){
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration ItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        ItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line));
        recyclerView.addItemDecoration(ItemDecoration);
    }

    public void listPasienBackToHome(View view) {
        startActivity(new Intent(this, AdminHome.class));
    }
}