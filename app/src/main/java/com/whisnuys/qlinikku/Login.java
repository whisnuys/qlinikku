package com.whisnuys.qlinikku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    String email, password, role;
    ProgressBar progressBar;
    Button btnLogin;
    FirebaseAuth auth;
    DatabaseReference dbF;
    FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.etLoginEmail);
        passwordEditText = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        progressBar = findViewById(R.id.progressbar1);
        progressBar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userLogin = firebaseAuth.getCurrentUser();
                if(userLogin != null){
                    dbF = FirebaseDatabase.getInstance().getReference().child("Users").child(userLogin.getUid());
                    dbF.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            role = snapshot.child("role").getValue().toString();
                            if (userLogin.isEmailVerified() && role.equals("pasien")){
                                startActivity(new Intent(Login.this, PasienHome.class));
                            } else if(userLogin.isEmailVerified() && role.equals("admin")) {
                                startActivity(new Intent(Login.this, AdminHome.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

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
        super.onStop();
        if(listener != null){
            auth.removeAuthStateListener(listener);
        }
    }

    private void loginAkun(){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    dbF = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());

                    dbF.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            role = snapshot.child("role").getValue().toString();
                            if (auth.getCurrentUser().isEmailVerified() && role.equals("pasien")){
                                Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, PasienHome.class));
                            } else if(auth.getCurrentUser().isEmailVerified() && role.equals("admin")) {
                                Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, AdminHome.class));
                            } else {
                                Toast.makeText(Login.this, "Email belum diverifikasi", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//                    if(auth.getCurrentUser().isEmailVerified()){
//                        Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(Login.this, PasienHome.class));
//                    } else {
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(Login.this, "Email belum diverifikasi", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Login Gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onClickToRegister(View view) {
        startActivity(new Intent(this, RegisterPasien.class));
    }
}