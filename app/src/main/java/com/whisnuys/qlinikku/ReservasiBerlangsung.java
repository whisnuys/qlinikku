package com.whisnuys.qlinikku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ReservasiBerlangsung extends AppCompatActivity {

    RecyclerView recyclerView;
    Button selesai;

    ReservasiBerlangsungAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservasi_berlangsung);

        selesai = findViewById(R.id.selesai);
        recyclerView = findViewById(R.id.appointments_recycler);

        Appointments();
    }
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private void Appointments() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<com.whisnuys.qlinikku.Models.ReservasiAktif> options =
                new FirebaseRecyclerOptions.Builder<com.whisnuys.qlinikku.Models.ReservasiAktif>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("active_app"),
                                com.whisnuys.qlinikku.Models.ReservasiAktif.class)
                        .build();

        adapter = new ReservasiBerlangsungAdapter(options, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void reservasiBackToHome(View view) {
        startActivity(new Intent(this, PasienHome.class));
    }
}