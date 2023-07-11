package com.whisnuys.qlinikku;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasienTentangKlinik extends AppCompatActivity {

    private ImageView imageKlinik;
    String namaKlinik, wa, email;
    private TextView tvNamaKlinik, tvAlamatKlinik, tvNoTelpKlinik, tvDeskripsiKlinik;
    private Button btnWaKlinik, btnEmailKlinik;

    private RewardedAd rewardedAd;
    private final String TAG = "TentangBackReward";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien_tentang_klinik);

        AdRequest adRequestReward = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequestReward, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                    }
                });

        tvNamaKlinik = findViewById(R.id.textViewNamaKlinik);
        tvAlamatKlinik = findViewById(R.id.textViewAlamatKlinik);
        tvNoTelpKlinik = findViewById(R.id.textViewNoTelpKlinik);
        tvDeskripsiKlinik = findViewById(R.id.textViewDeskripsiKlinik);
        btnWaKlinik = findViewById(R.id.buttonWaKlinik);
        btnEmailKlinik = findViewById(R.id.buttonEmailKlinik);
        imageKlinik = findViewById(R.id.imageViewTentangKlinik);

        DatabaseReference dbF = FirebaseDatabase.getInstance().getReference("klinik").child("tentang");

        dbF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                namaKlinik = snapshot.child("nama").getValue().toString();
                String deskripsi = snapshot.child("deskripsi").getValue().toString();
                String noTelp = snapshot.child("noTelp").getValue().toString();
                String alamat = snapshot.child("alamat").getValue().toString();
                email = snapshot.child("email").getValue().toString();
                wa = snapshot.child("wa").getValue().toString();
                String gambar = snapshot.child("gambar").getValue().toString().trim();


                if(isEmpty(gambar)){
                    imageKlinik.setImageResource(R.drawable.image_dummy);
                } else {
                    Glide.with(PasienTentangKlinik.this).load(gambar).into(imageKlinik);
                }

                tvNamaKlinik.setText(namaKlinik);
                tvDeskripsiKlinik.setText("    " + deskripsi);
                tvNoTelpKlinik.setText(noTelp);
                tvAlamatKlinik.setText(alamat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnWaKlinik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + "62" + wa + "&text=" + "Halo, saya ingin bertanya mengenai pelayanan di " + namaKlinik;
                try {
                    PackageManager pm = getApplicationContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(PasienTentangKlinik.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        btnEmailKlinik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedAd != null) {
                    Activity activityContext = PasienTentangKlinik.this;
                    rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d(TAG, "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });
    }

    public void tentangKlinikPasienBackToHome(View view) {

        startActivity(new Intent(this, PasienHome.class));
    }
}