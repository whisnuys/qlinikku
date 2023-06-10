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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whisnuys.qlinikku.Models.Dokter;
import com.whisnuys.qlinikku.Models.PersonDokter;

import java.util.ArrayList;
import java.util.List;

public class ListDataDokter extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecycleViewAdapterListDokter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference reference;
    private ArrayList<PersonDokter> dataDokter;

//    private FloatingActionButton fab, home;

    private EditText searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data_dokter);
        recyclerView = findViewById(R.id.datalistDokter);

        GetData();

//        searchView = findViewById(R.id.etSearchDokter);
//        searchView.addTextChangedListener(new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            if(s.toString().isEmpty()){
//                GetData(s.toString());
//            }
////                else {
////                    adapter.getFilter().filter(s);
////                }
//        }
//    });

    MyRecycleView();
}

    private void GetData(){
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataDokter = new ArrayList<>();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("role").getValue().toString().equals("dokter")){
                        PersonDokter dokter = snapshot.getValue(PersonDokter.class);

                        dokter.setUid(snapshot.getKey());
                        dataDokter.add(dokter);
                    }
                }

                adapter = new RecycleViewAdapterListDokter(dataDokter, ListDataDokter.this);
                recyclerView.setAdapter(adapter);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListDataDokter.this, "Data gagal dimuat", Toast.LENGTH_SHORT).show();
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

    public void listDokterBackToHome(View view) {
        startActivity(new Intent(this, AdminHome.class));
    }
}