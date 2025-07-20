package com.example.sunmail.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.Application;

import com.example.sunmail.R;
import com.example.sunmail.util.Resource;
import com.example.sunmail.util.Status;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.viewmodel.RegisterViewModel;

import java.io.IOException;
import java.util.Calendar;
import android.content.Context;
import android.util.Patterns;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstNameEdit, lastNameEdit, userNameEdit, emailEdit, passwordEdit, confirmPasswordEdit, birthDateEdit;
    private Spinner genderSpinner;
    private ImageView profileImage;
    private Button selectImageBtn, registerBtn;
    private ProgressBar progressBar;
    private TextView firstNameError, lastNameError, userNameError, emailError, passwordError, confirmPasswordError, genderError, birthDateError, generalError;
    private RegisterViewModel vm;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find views
        firstNameEdit = findViewById(R.id.first_name);
        lastNameEdit = findViewById(R.id.last_name);
        userNameEdit = findViewById(R.id.user_name);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        confirmPasswordEdit = findViewById(R.id.confirm_password);
        birthDateEdit = findViewById(R.id.birth_date);
        genderSpinner = findViewById(R.id.gender_spinner);
        profileImage = findViewById(R.id.profile_image);
        selectImageBtn = findViewById(R.id.select_image_btn);
        registerBtn = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progress_bar);

        firstNameError = findViewById(R.id.first_name_error);
        lastNameError = findViewById(R.id.last_name_error);
        userNameError = findViewById(R.id.user_name_error);
        emailError = findViewById(R.id.email_error);
        passwordError = findViewById(R.id.password_error);
        confirmPasswordError = findViewById(R.id.confirm_password_error);
        genderError = findViewById(R.id.gender_error);
        birthDateError = findViewById(R.id.birth_date_error);
        generalError = findViewById(R.id.general_error);

        // Date picker
        birthDateEdit.setOnClickListener(v -> showDatePicker());

        // Select image
        selectImageBtn.setOnClickListener(v -> openImagePicker());

        // ViewModel
        vm = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Register button
        registerBtn.setOnClickListener(v -> {
            clearErrors();
            UserRegisterForm form = new UserRegisterForm();
            form.firstName = firstNameEdit.getText().toString();
            form.lastName = lastNameEdit.getText().toString();
            form.userName = userNameEdit.getText().toString();
            form.email = emailEdit.getText().toString();
            form.password = passwordEdit.getText().toString();
            form.confirmPassword = confirmPasswordEdit.getText().toString();
            form.gender = genderSpinner.getSelectedItem().toString();
            form.birthDate = birthDateEdit.getText().toString();
            form.pictureUri = selectedImageUri;
            vm.register(form, this);
        });

        // Observe result
        vm.getResult().observe(this, res -> {
            if (res.status == Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
            if (res.status == Status.SUCCESS) {
                Toast.makeText(this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                finish();
            } else if (res.status == Status.ERROR) {
                showGeneralError(res.error);
            }
        });

        // Observe validation errors
        vm.getFirstNameError().observe(this, err -> showError(firstNameError, err));
        vm.getLastNameError().observe(this, err -> showError(lastNameError, err));
        vm.getUserNameError().observe(this, err -> showError(userNameError, err));
        vm.getEmailError().observe(this, err -> showError(emailError, err));
        vm.getPasswordError().observe(this, err -> showError(passwordError, err));
        vm.getConfirmPasswordError().observe(this, err -> showError(confirmPasswordError, err));
        vm.getGenderError().observe(this, err -> showError(genderError, err));
        vm.getBirthDateError().observe(this, err -> showError(birthDateError, err));
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format("%04d-%02d-%02d", year, month+1, dayOfMonth);
            birthDateEdit.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showError(TextView view, String error) {
        view.setText(error);
        view.setVisibility(error != null && !error.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showGeneralError(String error) {
        generalError.setText(error);
        generalError.setVisibility(error != null && !error.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void clearErrors() {
        showError(firstNameError, "");
        showError(lastNameError, "");
        showError(userNameError, "");
        showError(emailError, "");
        showError(passwordError, "");
        showError(confirmPasswordError, "");
        showError(genderError, "");
        showError(birthDateError, "");
        showGeneralError("");
    }
}
