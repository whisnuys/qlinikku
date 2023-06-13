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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
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
import com.whisnuys.qlinikku.Models.Dokter;
import com.whisnuys.qlinikku.Models.Pasien;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class EditDataDokter extends AppCompatActivity {

    EditText et_namalengkap, et_noHp;
    String[] listJK, listSpesialis;
    Spinner sp_jenisKelamin, sp_spesialisasi;
    ShapeableImageView imgAvatarDokter;
    Button btnSimpan;
    FloatingActionButton btnUbahFoto;
    private ProgressBar progressBar;

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data_dokter);

        et_namalengkap = findViewById(R.id.et_namalengkap_dokter);
        et_noHp = findViewById(R.id.new_et_noHP_dokter);
        sp_jenisKelamin = findViewById(R.id.new_spinnerJK_dokter);
        sp_spesialisasi = findViewById(R.id.new_spinnerSpesialis);
        imgAvatarDokter = findViewById(R.id.new_img_avatar_dokter);
        btnSimpan = findViewById(R.id.btn_update_dokter);
        btnUbahFoto = findViewById(R.id.fab_avatar_dokter);
        progressBar = findViewById(R.id.new_progressbar_dokter);
        progressBar.setVisibility(View.GONE);

        getData();

        btnUbahFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namaLengkap = et_namalengkap.getText().toString();
                String noHP = et_noHp.getText().toString();
                String jk = sp_jenisKelamin.getSelectedItem().toString();
                String spesialis = sp_spesialisasi.getSelectedItem().toString();


                if(isEmpty(namaLengkap)){
                    et_namalengkap.setError("Nama lengkap tidak boleh kosong");
                    et_namalengkap.requestFocus();
                } else if(isEmpty(noHP)){
                    et_noHp.setError("NoHp tidak boleh kosong");
                    et_noHp.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    imgAvatarDokter.setDrawingCacheEnabled(true);
                    imgAvatarDokter.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imgAvatarDokter.getDrawable()).getBitmap();
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
                                    Dokter setDokter = new Dokter();
                                    setDokter.setNamaLengkap(namaLengkap);
                                    setDokter.setNoTelepon(noHP);
                                    setDokter.setJenisKelamin(jk);
                                    setDokter.setSpesialis(spesialis);
                                    setDokter.setGambar(uri.toString());

                                    updateDokter(setDokter);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditDataDokter.this, "Update Gagal", Toast.LENGTH_SHORT).show();
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
        String dokterID = getIntent().getExtras().getString("dataDokterID");
        DatabaseReference dbF = FirebaseDatabase.getInstance().getReference("Dokter").child(dokterID);

        dbF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namaLengkap = snapshot.child("namaLengkap").getValue().toString();
                String noHP = snapshot.child("noTelepon").getValue().toString();
                String jk = snapshot.child("jenisKelamin").getValue().toString();
                String spesialis = snapshot.child("spesialis").getValue().toString();
                String gambar_avatar = snapshot.child("gambar").getValue().toString().trim();

                if (isEmpty(gambar_avatar)) {
                    imgAvatarDokter.setImageResource(R.drawable.avatarhome);
                } else {
                    Glide.with(EditDataDokter.this).load(gambar_avatar).into(imgAvatarDokter);
                }

                listJK = new String[]{"Laki-laki", "Perempuan"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditDataDokter.this, android.R.layout.simple_spinner_dropdown_item, listJK);
                sp_jenisKelamin.setAdapter(adapter);
                sp_jenisKelamin.setSelection(adapter.getPosition(jk));
                listSpesialis = new String[]{"Umum", "Penyakit Dalam", "Saraf", "Kandungan", "Kulit dan Kelamin", "THT", "Mata"};
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(EditDataDokter.this, android.R.layout.simple_spinner_dropdown_item, listSpesialis);
                sp_spesialisasi.setAdapter(adapter2);
                sp_spesialisasi.setSelection(adapter2.getPosition(spesialis));

                et_namalengkap.setText(namaLengkap);
                et_noHp.setText(noHP);
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
                    imgAvatarDokter.setVisibility(View.VISIBLE);
                    Bitmap bitmap =(Bitmap) data.getExtras().get("data");
                    imgAvatarDokter.setImageBitmap(bitmap);
                }
                break;

            case REQUEST_CODE_GALLERY:
                if(resultCode == RESULT_OK){
                    imgAvatarDokter.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    imgAvatarDokter.setImageURI(uri);
                }
                break;
        }
    }
    private void updateDokter(Dokter setDokter){
        String dokterID = getIntent().getExtras().getString("dataDokterID");
        DatabaseReference dbF = FirebaseDatabase.getInstance().getReference("Dokter").child(dokterID);

        dbF.setValue(setDokter).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                et_namalengkap.setText("");
                et_noHp.setText("");
                Toast.makeText(EditDataDokter.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditDataDokter.this, ListDataDokter.class));
                finish();
            }
        });
    }
    public void editDataBackToList(View view) {
        startActivity(new Intent(this, ListDataDokter.class));
    }
}