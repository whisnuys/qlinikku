package com.whisnuys.qlinikku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whisnuys.qlinikku.Models.Pasien;
import com.whisnuys.qlinikku.Models.Person;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterPasien extends AppCompatActivity {

     Button btnDatePicker, btnRegister;
     EditText selectedDate, namaLengkapEditText, nikEditText, alamatEditText, noHpEditText, emailEditText, passwordEditText;
     Spinner jenisKelamin;
     ProgressBar progressBar;
     String tglLahir, namaLengkap, nik, alamat, noHp, email, password, jk;
     DatePickerDialog datePickerDialog;
     FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_register_pasien);

        btnDatePicker = findViewById(R.id.btnPickDate);
        selectedDate = findViewById(R.id.etBirthdate);
        namaLengkapEditText = findViewById(R.id.etNamaLengkap);
        nikEditText = findViewById(R.id.etNIK);
        alamatEditText = findViewById(R.id.etAlamat);
        noHpEditText = findViewById(R.id.etNoHP);
        emailEditText = findViewById(R.id.etRegisterEmail);
        passwordEditText = findViewById(R.id.etRegisterPassword);
        jenisKelamin = findViewById(R.id.spinnerJK);
        btnRegister = findViewById(R.id.btnRegisterSubmit);
        progressBar = findViewById(R.id.progressbar2);
        progressBar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat simpledateformat = new SimpleDateFormat("dd MMM yyyy", new Locale("in", "ID"));
                Calendar calendar = Calendar.getInstance();
                Locale.setDefault(new Locale("in", "ID"));

                datePickerDialog = new DatePickerDialog(RegisterPasien.this, R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        selectedDate.setText(simpledateformat.format(newDate.getTime()));
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                cekDataPasien();
            }
        });
    }

    private void cekDataPasien(){
        namaLengkap = namaLengkapEditText.getText().toString().trim();
        nik = nikEditText.getText().toString().trim();
        alamat = alamatEditText.getText().toString().trim();
        noHp = noHpEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        jk = jenisKelamin.getSelectedItem().toString();
        tglLahir = selectedDate.getText().toString();

        if (namaLengkap.isEmpty()){
            namaLengkapEditText.setError("Nama Lengkap harus diisi");
            namaLengkapEditText.requestFocus();
            return;
        }
        if (nik.isEmpty()){
            nikEditText.setError("NIK harus diisi");
            nikEditText.requestFocus();
            return;
        }
        if (alamat.isEmpty()){
            alamatEditText.setError("Alamat harus diisi");
            alamatEditText.requestFocus();
            return;
        }
        if (noHp.isEmpty()){
            noHpEditText.setError("Nomor HP harus diisi");
            noHpEditText.requestFocus();
            return;
        }
        if (email.isEmpty()){
            emailEditText.setError("Email harus diisi");
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()){
            passwordEditText.setError("Password harus diisi");
            passwordEditText.requestFocus();
            return;
        }
        if (tglLahir.isEmpty()){
            selectedDate.setError("Tanggal Lahir harus diisi");
            selectedDate.requestFocus();
            return;
        }
        if (jk.isEmpty()){
            Toast.makeText(getApplicationContext(), "Jenis Kelamin harus diisi", Toast.LENGTH_LONG).show();
            jenisKelamin.requestFocus();
            return;
        } else {
            if(password.length() < 6){
                Toast.makeText(this, "Password terlalu pendek", Toast.LENGTH_SHORT).show();
            } else {
                buatAkunPasien();
            }
        }
    }

    private void buatAkunPasien(){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    String uid = auth.getCurrentUser().getUid();
                    Pasien user = new Pasien(namaLengkap, nik, "pasien",jk,tglLahir, alamat, noHp, email, password, "",uid);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(uid)
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(), "Registrasi Berhasil, Silahkan cek email untuk verifikasi", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Registrasi Gagal", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void onClickToLogin(View view) {
        startActivity(new Intent(this, Login.class));
    }
}