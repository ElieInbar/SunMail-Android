package com.example.sunmail.activities;

import static com.example.sunmail.R.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sunmail.R;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.viewmodel.RegisterViewModel;
import com.example.sunmail.model.AuthResult;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegisterViewModel vm = new ViewModelProvider(this).get(RegisterViewModel.class);

//        RegisterViewModel vm = new ViewModelProvider(this).get(RegisterViewModel.class);
//
//        // Find views
//        firstNameEdit = findViewById(R.id.et_first_name);
//        lastNameEdit = findViewById(R.id.et_last_name);
//        userNameEdit = findViewById(R.id.et_user_name);
//        emailEdit = findViewById(R.id.et_email);
//        passwordEdit = findViewById(R.id.password);
//        confirmPasswordEdit = findViewById(R.id.confirm_password);
//        birthDateEdit = findViewById(R.id.birth_date);
//        genderSpinner = findViewById(R.id.gender_spinner);
////        profileImage = findViewById(R.id.profile_image);
////        selectImageBtn = findViewById(R.id.select_image_btn);
//        registerBtn = findViewById(R.id.register_btn);
//        progressBar = findViewById(R.id.progress_bar);

        EditText editFirstName = findViewById(R.id.et_first_name);
        EditText editLastName = findViewById(R.id.et_last_name);
        Spinner spinnerGender = findViewById(R.id.gender_spinner);
        EditText editBirthDate = findViewById(R.id.birth_date);
        EditText editUserName = findViewById(R.id.et_user_name);
        EditText editPassword = findViewById(R.id.password);
        EditText editConfirmPassword = findViewById(R.id.confirm_password);
        Button registerBtn = findViewById(R.id.register_btn);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
//        Button selectProfilePictureButton = findViewById(R.id.buttonSelectProfilePicture);
//        TextView selectedProfilePictureText = findViewById(R.id.textSelectedProfilePicture);

        // ViewModel

//        birthDateEdit.setOnClickListener(v -> showDatePicker());
//
//        // Select image
//        selectImageBtn.setOnClickListener(v -> openImagePicker());
//
//        checkStoragePermission();
//
//        selectProfilePictureButton.setOnClickListener(v -> {
//            if (isStoragePermissionGranted()) {
//                openImagePicker();
//            } else {
//                requestStoragePermission();
//            }
//        });

//        vm.getAuthResult().observe(this, result -> {
//            if (result instanceof AuthResult.Success) {
//                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//            } else if (result instanceof AuthResult.Error) {
//                Toast.makeText(this, ((AuthResult.Error) result).getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });

        // Register button
        registerBtn.setOnClickListener(v -> {
            // Validate inputs
//        if (progressBar.getVisibility() == View.VISIBLE) return; // Prevent multiple clicks
//        progressBar.setVisibility(View.VISIBLE);
//
//        // Get input values
//        progressBar.setVisibility(View.VISIBLE);
//        selectedImageUri = null; // Reset image URI before validation
//
//        if (selectedImageFile != null) {
//            selectedImageUri = Uri.fromFile(selectedImageFile);
//        }

            // Collect input data
            String firstName = editFirstName.getText().toString();
            String lastName = editLastName.getText().toString();
            String gender = spinnerGender.getSelectedItem().toString();
            String birthDate = editBirthDate.getText().toString();
            String userName = editUserName.getText().toString();
            String password = editPassword.getText().toString();
            String confirmPassword = editConfirmPassword.getText().toString();

            if (firstName.isEmpty() || userName.isEmpty() || birthDate.isEmpty() ||
                    gender.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (gender.equals("Select Gender")) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            UserRegisterForm form = new UserRegisterForm(firstName, lastName, gender,
                    birthDate, userName, password, confirmPassword);
            vm.register(form);
        });

        vm.getAuthResult().observe(this, authResult -> {
            if (authResult instanceof AuthResult.Success) {
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                // Navigate to login after successful registration
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (authResult instanceof AuthResult.Error) {
                String errorMessage = ((AuthResult.Error) authResult).getMessage();
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

//    private void showDatePicker() {
//        final Calendar c = Calendar.getInstance();
//        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
//            String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
//            birthDateEdit.setText(date);
//        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        dpd.show();
//    }
//
//    private void openImagePicker() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
//            selectedImageUri = data.getData();
//            try {
//                selectedImageFile = new File(getPathFromUri(selectedImageUri));
//                selectedProfilePictureText.setText(selectedImageFile.getName());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void checkStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!isStoragePermissionGranted()) {
//                requestStoragePermission();
//            }
//        }
//    }
//    private boolean isStoragePermissionGranted() {
//        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
//                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_STORAGE_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private boolean validateInputs(String firstName, String lastName, String userName, String email, String password, String confirmPassword) {
//        if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (password.length() < 8) {
//            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        return true;
//    }
//
//    private String getPathFromUri(Uri uri) {
//        String filePath = null;
//        if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Cursor cursor = null;
//            try {
//                cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
//                if (cursor != null && cursor.moveToFirst()) {
//                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    filePath = cursor.getString(columnIndex);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            filePath = uri.getPath();
//        }
//        return filePath;
//    }
//
//}