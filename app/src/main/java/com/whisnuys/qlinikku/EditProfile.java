package com.whisnuys.qlinikku;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.whisnuys.qlinikku.Models.Pasien;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    String[] listJK;
    EditText et_namaLengkap, et_nik, et_tgllahir, et_alamat, et_nohp, et_email, et_password, et_role;
    Spinner spinnerJK;
    ShapeableImageView imgAvatar;
    Button btnTglLahir, btnSimpan;
    FloatingActionButton btnUbahFoto;
    DatePickerDialog datePickerDialog;
    ProgressBar progressBar;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        et_namaLengkap = findViewById(R.id.et_namalengkap);
        et_nik = findViewById(R.id.et_nik);
        et_tgllahir = findViewById(R.id.et_tgllahir);
        et_alamat = findViewById(R.id.et_alamat);
        et_nohp = findViewById(R.id.et_noHP);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_role = findViewById(R.id.et_role);
        et_email.setVisibility(View.GONE);
        et_password.setVisibility(View.GONE);
        et_role.setVisibility(View.GONE);
        spinnerJK = findViewById(R.id.new_spinnerJK);
        imgAvatar = findViewById(R.id.img_avatar);
        btnTglLahir = findViewById(R.id.btn_tgllahir);
        btnSimpan = findViewById(R.id.btn_editprofile);
        btnUbahFoto = findViewById(R.id.fab_avatar);
        progressBar = findViewById(R.id.progressbar_editprofile);
        progressBar.setVisibility(View.GONE);


        getData();

        btnUbahFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });


        btnTglLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat simpledateformat = new SimpleDateFormat("dd MMM yyyy", new Locale("in", "ID"));
                Calendar calendar = Calendar.getInstance();
                Locale.setDefault(new Locale("in", "ID"));

                datePickerDialog = new DatePickerDialog(EditProfile.this, R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        et_tgllahir.setText(simpledateformat.format(newDate.getTime()));
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namaLengkap = et_namaLengkap.getText().toString();
                String nik = et_nik.getText().toString();
                String tglLahir = et_tgllahir.getText().toString();
                String alamat = et_alamat.getText().toString();
                String noHP = et_nohp.getText().toString();
                String jk = spinnerJK.getSelectedItem().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                String role = et_role.getText().toString();

                if(isEmpty(namaLengkap)){
                    et_namaLengkap.setError("Nama lengkap tidak boleh kosong");
                    et_namaLengkap.requestFocus();
                } else if(isEmpty(nik)){
                    et_nik.setError("NIK tidak boleh kosong");
                    et_nik.requestFocus();
                } else if(isEmpty(tglLahir)){
                    et_tgllahir.setError("Tanggal lahir tidak boleh kosong");
                    et_tgllahir.requestFocus();
                } else if(isEmpty(alamat)){
                    et_alamat.setError("Alamat tidak boleh kosong");
                    et_alamat.requestFocus();
                } else if(isEmpty(noHP)){
                    et_nohp.setError("No HP tidak boleh kosong");
                    et_nohp.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    imgAvatar.setDrawingCacheEnabled(true);
                    imgAvatar.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
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
                                    Pasien setPasien = new Pasien();
                                    setPasien.setNamaLengkap(namaLengkap);
                                    setPasien.setNik(nik);
                                    setPasien.setTanggalLahir(tglLahir);
                                    setPasien.setAlamat(alamat);
                                    setPasien.setNoHp(noHP);
                                    setPasien.setJenisKelamin(jk);
                                    setPasien.setEmail(email);
                                    setPasien.setPassword(password);
                                    setPasien.setRole(role);
                                    setPasien.setGambar(uri.toString());

                                    updatePasien(setPasien);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditProfile.this, "Update Gagal", Toast.LENGTH_SHORT).show();
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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference dbF = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        dbF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namaLengkap = snapshot.child("namaLengkap").getValue().toString();
                String nik = snapshot.child("nik").getValue().toString();
                String tglLahir = snapshot.child("tanggalLahir").getValue().toString();
                String alamat = snapshot.child("alamat").getValue().toString();
                String noHP = snapshot.child("noHp").getValue().toString();
                String jk = snapshot.child("jenisKelamin").getValue().toString();
                String gambar_avatar = snapshot.child("gambar").getValue().toString().trim();
                String email = snapshot.child("email").getValue().toString();
                String password = snapshot.child("password").getValue().toString();
                String role = snapshot.child("role").getValue().toString();

                if(isEmpty(gambar_avatar)){
                    imgAvatar.setImageResource(R.drawable.avatarhome);
                } else {
                    Glide.with(EditProfile.this).load(gambar_avatar).into(imgAvatar);
                }

                listJK = new String[]{"Laki-laki", "Perempuan"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProfile.this, android.R.layout.simple_spinner_dropdown_item, listJK);
                spinnerJK.setAdapter(adapter);
                spinnerJK.setSelection(adapter.getPosition(jk));

                et_namaLengkap.setText(namaLengkap);
                et_nik.setText(nik);
                et_tgllahir.setText(tglLahir);
                et_alamat.setText(alamat);
                et_nohp.setText(noHP);
                et_email.setText(email);
                et_password.setText(password);
                et_role.setText(role);
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
                    imgAvatar.setVisibility(View.VISIBLE);
                    Bitmap bitmap =(Bitmap) data.getExtras().get("data");
                    imgAvatar.setImageBitmap(bitmap);
                }
                break;

            case REQUEST_CODE_GALLERY:
                if(resultCode == RESULT_OK){
                    imgAvatar.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    imgAvatar.setImageURI(uri);
                }
                break;
        }
    }

    private void updatePasien(Pasien setPasien){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference dbF = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        dbF.setValue(setPasien).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                et_namaLengkap.setText("");
                et_nik.setText("");
                et_tgllahir.setText("");
                et_alamat.setText("");
                et_nohp.setText("");
                Toast.makeText(EditProfile.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditProfile.this, PasienHome.class));
                finish();
            }
        });
    }

    public void backToHome(View view) {
        startActivity(new Intent(this, PasienHome.class));
    }
}