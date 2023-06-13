package com.whisnuys.qlinikku;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.whisnuys.qlinikku.Models.Dokter;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.UUID;

public class TambahDokter extends AppCompatActivity {
    Button btnTambahDokter;

    FloatingActionButton btnUbahFoto;
    ShapeableImageView imgAvatarDokter;
    ProgressBar progressBar;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

//    private HashSet<String> specializations = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_dokter);

        btnUbahFoto = findViewById(R.id.fab_avatar_dokter);
        imgAvatarDokter = findViewById(R.id.img_avatar_dokter);
        progressBar = findViewById(R.id.progressbar_dokter);
        progressBar.setVisibility(View.GONE);

        btnUbahFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        Spinner jkSpinner = (Spinner)findViewById(R.id.spinnerJK_dokter);
        ArrayAdapter<String> genderSpinnerAdapter = new ArrayAdapter<String>(TambahDokter.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.spinner_jk));
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jkSpinner.setAdapter(genderSpinnerAdapter);

        Spinner spesialisSpinner = (Spinner)findViewById(R.id.spinnerSpesialis);
        ArrayAdapter<String> splSpinner = new ArrayAdapter<String>(TambahDokter.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.SelectSpesialis));
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spesialisSpinner.setAdapter(splSpinner);

        // Specialist dialog list
//        Button btnSelectSpesialis = (Button) findViewById(R.id.btn_pilih_spesialis);
//        btnSelectSpesialis.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                MultipleSelectionFragment selectionDialog = new MultipleSelectionFragment(getResources().getStringArray(R.array.SelectSpesialis), "Pilih Spesialis");
//                selectionDialog.show(getSupportFragmentManager(), "PilihSpesialisDialog");
//                specializations = selectionDialog.getSelectedItems();
//            }
//        });

        btnTambahDokter = findViewById(R.id.btn_tambah_dokter);
        btnTambahDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahDokter();
            }
        });
    }

    private void tambahDokter() {
        EditText etNamaLengkap = findViewById(R.id.et_namalengkap_dokter);
        EditText etNoHP = findViewById(R.id.et_noHP_dokter);
        String namaLengkap = etNamaLengkap.getText().toString().trim();
        String noHP = etNoHP.getText().toString().trim();

        Spinner jkSpinner = (Spinner) findViewById(R.id.spinnerJK_dokter);
        String jenisKelamin = jkSpinner.getSelectedItem().toString();

        Spinner spesialisSpinner = (Spinner) findViewById(R.id.spinnerSpesialis);
        String spesialis = spesialisSpinner.getSelectedItem().toString();

        if(namaLengkap.isEmpty()) {
            etNamaLengkap.setError("Empty first name");
            return;
        }
        if(noHP.isEmpty()) {
            etNoHP.setError("Empty last name");
            return;
        }

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
                        final String pushId = FirebaseDatabase.getInstance().getReference().push().getKey();
                        Dokter dokter = new Dokter(namaLengkap, jenisKelamin, noHP, spesialis,uri.toString().trim(), pushId);
                        progressBar.setVisibility(View.GONE);
                        FirebaseDatabase.getInstance().getReference("Dokter").child(pushId).setValue(dokter);
                        etNamaLengkap.setText("");
                        etNoHP.setText("");
                        Toast.makeText(TambahDokter.this, "Tambah Dokter Berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TambahDokter.this, AdminHome.class));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TambahDokter.this, "Tambah Gagal", Toast.LENGTH_SHORT).show();
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
    public void tambahDokterBackToHome(View view) {
        startActivity(new Intent(this, AdminHome.class));
    }

}