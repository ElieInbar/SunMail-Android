package com.example.sunmail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.sunmail.R;
import com.example.sunmail.model.AuthResult;
import com.example.sunmail.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        LoginViewModel vm = new ViewModelProvider(this).get(LoginViewModel.class);


        EditText editUserName = findViewById(R.id.editTextUserName);
        EditText editPassword = findViewById(R.id.editTextPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        vm.isLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                goToMain();
            }
        });

        btnRegister.setOnClickListener(v -> {
            // move to register activity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
                String userNameOrEmail = editUserName.getText().toString();
                String password = editPassword.getText().toString();

                if (userNameOrEmail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Username and Password are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                vm.login(userNameOrEmail, password);
            });

            vm.getAuthResult().observe(this, authResult -> {
                progressBar.setVisibility(View.GONE);
                if (authResult instanceof AuthResult.Success) {
                    // TODO - Save cookie to room
                    goToMain();

                } else if (authResult instanceof AuthResult.Error) {
                    String errorMessage = ((AuthResult.Error) authResult).getMessage();
                    // Show error message to the user
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
    }
    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
