package com.whisnuys.qlinikku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

import java.util.ArrayList;

public class PasienHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien_home);

        ImageButton imageButton = findViewById(R.id.avatarHome);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference dbF = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        TextView mName = mNavigationView.getHeaderView(0).findViewById(R.id.profil_nama);
        TextView mEmail = mNavigationView.getHeaderView(0).findViewById(R.id.profil_email);
        TextView namaHome = findViewById(R.id.namaHome);

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


        ImageSlider imageSliderHome = findViewById(R.id.imageSliderHome);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.image_slider1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image_slider1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image_slider1, ScaleTypes.FIT));
        imageSliderHome.setImageList(slideModels,ScaleTypes.FIT);
    }
}