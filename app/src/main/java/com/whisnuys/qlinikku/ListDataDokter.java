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

    protected boolean checkSpecializations (List<String> a, List<String> b){
        for (String s: b){
            if (!a.contains(s)){
                return false;
            }
        }
        return true;
    }

    private Dokter selectedDoctor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data_dokter);

        //Setting up ListView of Doctors
        ListView doctorView = (ListView) findViewById(R.id.datalistDokter);
        final ArrayList<String> printDoctors = new ArrayList<String>();
        final ArrayList<PersonDokter> doctors = new ArrayList<PersonDokter>();
        final ArrayAdapter doctorAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, printDoctors);
        //The format can be: "Dr. NAME\nGENDER\nSPECIALIZATION"
//        doctors.add("Dr. Eric Zhou\nMale\nCardiology");

        //Getting filter options
        String gender = getIntent().getStringExtra("jenisKelamin"); //null if DNE
        String spesialis = getIntent().getStringExtra("spesialis");

        //Updating filter textView
        String genderText;
        if (gender == null){
            genderText = "";
        } else { genderText=gender; }
        String spesialisText;
        if (spesialis == null){
            spesialisText = "";
        } else { spesialisText=spesialis; }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String type = child.child("role").getValue(String.class);
                    String userjenisKelamin = child.child("jenisKelamin").getValue(String.class);
                    String userSpesialis = child.child("spesialis").getValue(String.class);

                    if (type.equals("dokter")){
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Dr. ");
                        stringBuilder.append(child.child("namaLengkap").getValue(String.class));
                        stringBuilder.append("\n");
                        stringBuilder.append(child.child("jenisKelamin").getValue(String.class));
                        stringBuilder.append("\n");
                        stringBuilder.append(child.child("noTelepon").getValue(String.class));
                        stringBuilder.append("\n");
                        stringBuilder.append(child.child("spesialis").getValue(String.class));
                        printDoctors.add(stringBuilder.toString());
                        doctors.add(child.getValue(PersonDokter.class));
                    }
                }
                doctorView.setAdapter(doctorAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        doctorView.setAdapter(doctorAdapter);

        //Setting up listener for when item is clicked.
        doctorView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Toast.makeText(ListDataDokter.this, "Selected Dr. " + doctors.get(index).toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AdminHome.class);
                intent.putExtra("doctor", printDoctors.get(index).toString());
                intent.putExtra("doctorID", doctors.get(index).getUid());
                startActivity(intent);
            }
        });
    }

//    public void onFilterStartButtonClicked (View view){
//        Intent intent = new Intent(this, AppointmentFilterOptionsActivity.class);
//        startActivity(intent);
//    }
}

//    private RecyclerView recyclerView;
//    RecycleViewAdapterListDokter adapter;
//    private RecyclerView.LayoutManager layoutManager;
//
//    private DatabaseReference reference;
//    private ArrayList<PersonDokter> dataDokter;
//
//    private EditText searchView;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_list_data_dokter);
//        GetData("");
//
//        searchView = findViewById(R.id.etSearchDokter);
//        searchView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.toString().isEmpty()){
//                    GetData(s.toString());
//                } else {
//                    adapter.getFilter().filter(s);
//                }
//            }
//        });
//
//        MyRecycleView();
//
//    }
//
//    private void GetData(String data){
//        reference = FirebaseDatabase.getInstance().getReference();
//        reference.child("Users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                dataDokter = new ArrayList<>();
//                dataDokter.clear();
//                for(DataSnapshot child : dataSnapshot.getChildren()){
//                    PersonDokter personDokter = child.getValue(PersonDokter.class);
//                    if(personDokter.getRole().equals("dokter")){
//                        personDokter.setUid(child.getKey());
//                        dataDokter.add(personDokter);
//                    }
//
//                }
//
//                adapter = new RecycleViewAdapterListDokter(dataDokter, ListDataDokter.this);
//                adapter.notifyDataSetChanged();
//
//                recyclerView.setAdapter(adapter);
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ListDataDokter.this, "Data gagal dimuat", Toast.LENGTH_SHORT).show();
//                Log.e("MyListActivity", error.getDetails() + " " + error.getMessage());
//            }
//        });
//    }
//
//    private void MyRecycleView(){
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//
//        DividerItemDecoration ItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
//        ItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line));
//        recyclerView.addItemDecoration(ItemDecoration);
//    }
//}