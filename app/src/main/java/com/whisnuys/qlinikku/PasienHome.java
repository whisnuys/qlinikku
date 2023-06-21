package com.whisnuys.qlinikku;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PasienHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien_home);

        ShapeableImageView imageButton = findViewById(R.id.avatar_img);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        CardView cardViewPilihDokter = findViewById(R.id.cardViewPilihDokter);
        cardViewPilihDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasienHome.this, PilihDokter.class));
                
            }
        });

        CardView cardViewReservasi = findViewById(R.id.cardViewReservasi);
        cardViewReservasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasienHome.this, ReservasiBerlangsung.class));

            }
        });

        CardView cardViewRiwayat = findViewById(R.id.cardViewRiwayat);
        cardViewRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasienHome.this, RiwayatReservasiPasien.class));

            }
        });


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference dbF = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        TextView mName = mNavigationView.getHeaderView(0).findViewById(R.id.profil_nama);
        TextView mEmail = mNavigationView.getHeaderView(0).findViewById(R.id.profil_email);
        ShapeableImageView mAvatarNav = mNavigationView.getHeaderView(0).findViewById(R.id.avatar_img_nav);
        ShapeableImageView mAvatar = findViewById(R.id.avatar_img);
        TextView namaHome = findViewById(R.id.namaHome);

        mNavigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });
        mNavigationView.getMenu().findItem(R.id.nav_profile).setOnMenuItemClickListener(menuItem -> {
            startActivity(new Intent(this, EditProfile.class));
            return true;
        });

        dbF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mName.setText(snapshot.child("namaLengkap").getValue().toString());
                namaHome.setText(snapshot.child("namaLengkap").getValue().toString());
                mEmail.setText(snapshot.child("email").getValue().toString());
                if (isEmpty(snapshot.child("gambar").getValue().toString())){
                    mAvatar.setImageResource(R.drawable.avatarhome);
                    mAvatarNav.setImageResource(R.drawable.avatarhome);
                } else {
                    Glide.with(getApplicationContext())
                            .load(snapshot.child("gambar").getValue().toString().trim())
                            .into(mAvatar);
                    Glide.with(getApplicationContext())
                            .load(snapshot.child("gambar").getValue().toString().trim())
                            .into(mAvatarNav);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ImageSlider imageSliderHome = findViewById(R.id.imageSliderHome);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.image_slider1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image_slider1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image_slider1, ScaleTypes.FIT));
        imageSliderHome.setImageList(slideModels,ScaleTypes.FIT);
    }

    private void logout(){FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(PasienHome.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}