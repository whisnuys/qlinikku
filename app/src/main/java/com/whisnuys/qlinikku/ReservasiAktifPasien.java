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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whisnuys.qlinikku.Models.ReservasiAktif;

public class ReservasiAktifPasien extends AppCompatActivity {

    RecyclerView recyclerView;
    Button selesai;

    ReservasiAktifPasienAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservasi_aktif_pasien);

        selesai = findViewById(R.id.selesai);
        recyclerView = findViewById(R.id.reservasiAktifPasien_recycler);
        String pasienID = getIntent().getExtras().getString("dataPasienID");

        Appointments(pasienID);
    }

    private void Appointments(String pasienID) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ReservasiAktif> options =
                new FirebaseRecyclerOptions.Builder<com.whisnuys.qlinikku.Models.ReservasiAktif>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").child(pasienID).child("active_app"),
                                com.whisnuys.qlinikku.Models.ReservasiAktif.class)
                        .build();
        Log.e("ReservasiAktifPasien", "Appointments: " + pasienID);
        adapter = new ReservasiAktifPasienAdapter(options, this, pasienID);
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

    public void reservasiPasienBackToHome(View view) {
        startActivity(new Intent(this, ListDataPasien.class));
    }
}