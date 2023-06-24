package com.whisnuys.qlinikku;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.whisnuys.qlinikku.Models.TentangKlinik;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class InputTentangKlinik extends AppCompatActivity {

    private EditText etNamaKlinik, deskripsiKlinik, etNoTelpKlinik, etAlamatKlinik, etEmailKlinik, etWaKlinik;
    private Button btnSimpan;
    private ImageView imageKlinik;
    private ProgressBar progressBar;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_tentang_klinik);

        etNamaKlinik = findViewById(R.id.et_namaKlinik);
        deskripsiKlinik = findViewById(R.id.et_deskripsiKlinik);
        etNoTelpKlinik = findViewById(R.id.et_noTelpKlinik);
        etAlamatKlinik = findViewById(R.id.et_alamatKlinik);
        etEmailKlinik = findViewById(R.id.et_emailKlinik);
        etWaKlinik = findViewById(R.id.et_noWhatsappCS);
        btnSimpan = findViewById(R.id.btnSimpanTentangKlinik);
        imageKlinik = findViewById(R.id.imageViewKlinik);
        progressBar = findViewById(R.id.progressbar_tentangKlinik);
        progressBar.setVisibility(View.GONE);
        DatabaseReference cekdbF = FirebaseDatabase.getInstance().getReference("klinik").child("tentang");
        cekdbF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    getData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageKlinik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String namaKlinik = etNamaKlinik.getText().toString().trim();
                String deskripsi = deskripsiKlinik.getText().toString().trim();
                String noTelp = etNoTelpKlinik.getText().toString().trim();
                String alamat = etAlamatKlinik.getText().toString().trim();
                String email = etEmailKlinik.getText().toString().trim();
                String wa = etWaKlinik.getText().toString().trim();

                if(deskripsi.isEmpty()){
                    deskripsiKlinik.setError("Deskripsi Klinik harus diisi");
                    deskripsiKlinik.requestFocus();
                } else if(namaKlinik.isEmpty()){
                    etNamaKlinik.setError("Nama Klinik harus diisi");
                    etNamaKlinik.requestFocus();
                } else if(noTelp.isEmpty()){
                    etNoTelpKlinik.setError("No Telepon Klinik harus diisi");
                    etNoTelpKlinik.requestFocus();
                } else if(alamat.isEmpty()){
                    etAlamatKlinik.setError("Alamat Klinik harus diisi");
                    etAlamatKlinik.requestFocus();
                } else if(email.isEmpty()){
                    etEmailKlinik.setError("Email Klinik harus diisi");
                    etEmailKlinik.requestFocus();
                } else if(wa.isEmpty()){
                    etWaKlinik.setError("No Whatsapp Klinik harus diisi");
                    etWaKlinik.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    imageKlinik.setDrawingCacheEnabled(true);
                    imageKlinik.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imageKlinik.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    String namaFile = UUID.randomUUID() + ".jpg";
                    final String lokasiFile = "gambar/" + namaFile;
                    UploadTask uploadTask = FirebaseStorage.getInstance().getReference(lokasiFile).putBytes(data);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    TentangKlinik tentangKlinik = new TentangKlinik();
                                    tentangKlinik.setNama(namaKlinik);
                                    tentangKlinik.setDeskripsi(deskripsi);
                                    tentangKlinik.setNoTelp(noTelp);
                                    tentangKlinik.setAlamat(alamat);
                                    tentangKlinik.setEmail(email);
                                    tentangKlinik.setWa(wa);
                                    tentangKlinik.setGambar(uri.toString());

                                    DatabaseReference dbFKlinik = FirebaseDatabase.getInstance().getReference("klinik").child("tentang");
                                    dbFKlinik.setValue(tentangKlinik).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.GONE);
                                            deskripsiKlinik.setText("");
                                            Toast.makeText(InputTentangKlinik.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(InputTentangKlinik.this, AdminHome.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(InputTentangKlinik.this, "Data gagal disimpan", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            progressBar.setVisibility(View.VISIBLE);
                            double progress = (100.0 * snapshot.getBytesTransferred())/ snapshot.getTotalByteCount();
                            progressBar.setProgress((int) progress);
                        }
                    });
                }
            }
        });
    }

    private void getData() {
        DatabaseReference dbF = FirebaseDatabase.getInstance().getReference("klinik").child("tentang");

        dbF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namaKlinik = snapshot.child("nama").getValue().toString();
                String deskripsi = snapshot.child("deskripsi").getValue().toString();
                String noTelp = snapshot.child("noTelp").getValue().toString();
                String alamat = snapshot.child("alamat").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String wa = snapshot.child("wa").getValue().toString();
                String gambar = snapshot.child("gambar").getValue().toString().trim();


                if(isEmpty(gambar)){
                    imageKlinik.setImageResource(R.drawable.image_dummy);
                } else {
                    Glide.with(InputTentangKlinik.this).load(gambar).into(imageKlinik);
                }

                etNamaKlinik.setText(namaKlinik);
                deskripsiKlinik.setText(deskripsi);
                etNoTelpKlinik.setText(noTelp);
                etAlamatKlinik.setText(alamat);
                etEmailKlinik.setText(email);
                etWaKlinik.setText(wa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getImage(){
        Intent imageIntentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageIntentGallery, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                if(resultCode == RESULT_OK){
                    imageKlinik.setVisibility(View.VISIBLE);
                    Bitmap bitmap =(Bitmap) data.getExtras().get("data");
                    imageKlinik.setImageBitmap(bitmap);
                }
                break;

            case REQUEST_CODE_GALLERY:
                if(resultCode == RESULT_OK){
                    imageKlinik.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    imageKlinik.setImageURI(uri);
                }
                break;
        }
    }

    public void tentangKlinikBackToHome(View view) {
        startActivity(new Intent(this, AdminHome.class));
    }
}