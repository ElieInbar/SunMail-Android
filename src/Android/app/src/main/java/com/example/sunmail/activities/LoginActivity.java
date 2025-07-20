package com.example.sunmail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.sunmail.R;
import com.example.sunmail.model.AuthResult;
import com.example.sunmail.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        LoginViewModel vm = new ViewModelProvider(this).get(LoginViewModel.class);
        EditText editUserName = findViewById(R.id.editTextUserName);
        EditText editPassword = findViewById(R.id.editTextPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            // move to register activity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String email = editUserName.getText().toString();
            String password = editPassword.getText().toString();
            vm.login(email, password);
            vm.getAuthResult().observe(this, authResult -> {
                if (authResult instanceof AuthResult.Success) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (authResult instanceof AuthResult.Error) {
                    String errorMessage = ((AuthResult.Error) authResult).getMessage();
                    // Show error message to the user
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
