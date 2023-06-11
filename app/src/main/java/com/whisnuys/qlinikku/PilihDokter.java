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

import java.util.ArrayList;

public class PilihDokter extends AppCompatActivity {

    private RecyclerView recyclerView;
    PilihDokterAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference reference;
    private ArrayList<Dokter> dataDokter;

    private EditText searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_dokter);
        recyclerView = findViewById(R.id.dataPilihDokter);

        GetData("");

        searchView = findViewById(R.id.etSearchPilihDokter);
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
        reference = FirebaseDatabase.getInstance().getReference("Dokter");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataDokter = new ArrayList<>();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Dokter dokter = snapshot.getValue(Dokter.class);

                    dokter.setUid(snapshot.getKey());
                    dataDokter.add(dokter);

                }

                adapter = new PilihDokterAdapter(dataDokter, PilihDokter.this);
                recyclerView.setAdapter(adapter);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PilihDokter.this, "Data gagal dimuat", Toast.LENGTH_SHORT).show();
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

    public void pilihDokterBackToHome(View view) {
        startActivity(new Intent(this, PasienHome.class));
    }
}