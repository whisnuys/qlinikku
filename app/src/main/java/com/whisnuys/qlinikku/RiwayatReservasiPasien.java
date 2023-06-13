package com.whisnuys.qlinikku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.whisnuys.qlinikku.Models.ReservasiAktif;
import com.whisnuys.qlinikku.Models.RiwayatReservasi;

public class RiwayatReservasiPasien extends AppCompatActivity {

    RecyclerView recyclerView;

    RiwayatReservasiPasienAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_reservasi_pasien);

        recyclerView = findViewById(R.id.riwayat_recycler);

        Appointments();
    }

    private void Appointments() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String pasienID = firebaseUser.getUid();

        FirebaseRecyclerOptions<RiwayatReservasi> options =
                new FirebaseRecyclerOptions.Builder<com.whisnuys.qlinikku.Models.RiwayatReservasi>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").child(pasienID).child("riwayat"),
                                com.whisnuys.qlinikku.Models.RiwayatReservasi.class)
                        .build();
        adapter = new RiwayatReservasiPasienAdapter(options, this);
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

    public void riwayatBackToHome(View view) {
        startActivity(new Intent(this, PasienHome.class));
    }
}