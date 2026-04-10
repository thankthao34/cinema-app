package com.example.cinemaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;
import com.example.cinemaapp.services.FirestoreService;
import com.example.cinemaapp.utils.AuthErrorMapper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {
    private final FirestoreService firestoreService = new FirestoreService();
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui long dien day du thong tin", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        Toast.makeText(this, "Khong lay duoc thong tin user", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseMessaging.getInstance().getToken()
                            .addOnSuccessListener(token -> firestoreService
                                    .saveOrUpdateUserProfile(user.getUid(), name, email, token)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Dang ky thanh cong", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()))
                            .addOnFailureListener(e -> firestoreService
                                    .saveOrUpdateUserProfile(user.getUid(), name, email, "")
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Dang ky thanh cong", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show()));
                })
                .addOnFailureListener(e -> Toast.makeText(this, AuthErrorMapper.toUserMessage(e), Toast.LENGTH_LONG).show());
    }
}
