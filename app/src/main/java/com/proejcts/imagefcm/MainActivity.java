package com.proejcts.imagefcm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText et_email, et_pass;
    Button btn_login;
    FirebaseAuth firebaseAuth;
    String email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);

        firebaseAuth = FirebaseAuth.getInstance();
        btn_login.setOnClickListener(v -> {
            email = et_email.getText().toString().trim();
            pass = et_pass.getText().toString().trim();
            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(MainActivity.this, AdminPanel.class));
//
                    }
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, AdminPanel.class));
        }

    }
}