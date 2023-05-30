package com.whisnuys.qlinikku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    String email, password;
    Button btnLogin;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.etLoginEmail);
        passwordEditText = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        auth = FirebaseAuth.getInstance();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userLogin = auth.getCurrentUser();
//                if(userLogin.getEmail() == "admin@qlinikku.com"){
//                    startActivity(new Intent(Login.this, AdminHome.class));
//                    finish();
//                }
                if (userLogin != null && userLogin.isEmailVerified()){
                    startActivity(new Intent(Login.this, PasienHome.class));
                    finish();
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();

                if (email.isEmpty()){
                    emailEditText.setError("Email tidak boleh kosong");
                    emailEditText.requestFocus();
                } else if (password.isEmpty()){
                    passwordEditText.setError("Password tidak boleh kosong");
                    passwordEditText.requestFocus();
                } else if (email.isEmpty() && password.isEmpty()){
                    emailEditText.setError("Email tidak boleh kosong");
                    emailEditText.requestFocus();
                    passwordEditText.setError("Password tidak boleh kosong");
                    passwordEditText.requestFocus();
                } else {
                    loginAkun();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if(listener != null){
            auth.removeAuthStateListener(listener);
        }
        super.onStop();

    }

    private void loginAkun(){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if (auth.getCurrentUser().getEmail().trim() == "admin@qlinikku.com"){
                        Toast.makeText(Login.this, "Login Admin Berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, AdminHome.class));
                    }
                    else if(auth.getCurrentUser().isEmailVerified()){
                        Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, PasienHome.class));
                    } else {
                        Toast.makeText(Login.this, "Email belum diverifikasi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Login Gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onClickToRegister(View view) {
        startActivity(new Intent(this, RegisterPasien.class));
    }
}