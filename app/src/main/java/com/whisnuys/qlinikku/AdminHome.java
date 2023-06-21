package com.whisnuys.qlinikku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whisnuys.qlinikku.Models.PersonDokter;

import java.util.ArrayList;

public class AdminHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        ImageButton imageButton = findViewById(R.id.avatarHomeAdmin);
        CardView cardViewTambahDokter = findViewById(R.id.cardViewDTambahDokter);
        cardViewTambahDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHome.this, TambahDokter.class));

            }
        });

        CardView cardViewDataDokter = findViewById(R.id.cardViewDataDokter);
        cardViewDataDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AdminHome.this, ListDataDokter.class));

            }
        });

        CardView cardViewDataPasien = findViewById(R.id.cardViewDataPasien);
        cardViewDataPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AdminHome.this, ListDataPasien.class));

            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_admin);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference dbF = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        NavigationView mNavigationView = findViewById(R.id.nav_view_admin);
        TextView mName = mNavigationView.getHeaderView(0).findViewById(R.id.profil_nama_admin);
        TextView mEmail = mNavigationView.getHeaderView(0).findViewById(R.id.profil_email_admin);
        TextView namaHome = findViewById(R.id.namaHomeAdmin);

        mNavigationView.getMenu().findItem(R.id.nav_logout_admin).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });

        dbF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mName.setText(snapshot.child("namaLengkap").getValue().toString());
                namaHome.setText(snapshot.child("namaLengkap").getValue().toString());
                mEmail.setText(snapshot.child("email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void logout(){FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(AdminHome.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}