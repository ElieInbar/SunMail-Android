package com.example.sunmail.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.sunmail.R;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.util.AvatarColorHelper;
import com.example.sunmail.viewmodel.RegisterViewModel;
import com.example.sunmail.model.AuthResult;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView profileImageView;
    private TextView profileInitial;
    private static final int PERMISSION_REQUEST_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RegisterViewModel vm = new ViewModelProvider(this).get(RegisterViewModel.class);

        EditText editFirstName = findViewById(R.id.et_first_name);
        EditText editLastName = findViewById(R.id.et_last_name);
        Spinner spinnerGender = findViewById(R.id.gender_spinner);
        EditText editBirthDate = findViewById(R.id.birth_date);
        EditText editUserName = findViewById(R.id.et_user_name);
        EditText editPassword = findViewById(R.id.password);
        EditText editConfirmPassword = findViewById(R.id.confirm_password);
        Button registerBtn = findViewById(R.id.register_btn);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        profileImageView = findViewById(R.id.profileImageView);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        });
        profileInitial = findViewById(R.id.profileInitial);

        editBirthDate.setFocusable(false);
        editBirthDate.setOnClickListener(v -> showDatePicker(editBirthDate));

        updateProfileInitial(editUserName.getText().toString());
        editUserName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) updateProfileInitial(editUserName.getText().toString());
        });
        editUserName.setOnKeyListener((v, keyCode, event) -> {
            updateProfileInitial(editUserName.getText().toString());
            return false;
        });

        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        registerBtn.setOnClickListener(v -> {

            String firstName = editFirstName.getText().toString();
            String lastName = editLastName.getText().toString();
            String gender = spinnerGender.getSelectedItem().toString();
            String birthDate = editBirthDate.getText().toString();
            String userName = editUserName.getText().toString();
            String password = editPassword.getText().toString();
            String confirmPassword = editConfirmPassword.getText().toString();

            if (firstName.isEmpty() || userName.isEmpty() || birthDate.isEmpty() ||
                    gender.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (gender.equals("Select Gender")) {
                Toast.makeText(this, getString(R.string.please_select_gender), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            registerBtn.setEnabled(false);
            UserRegisterForm form = new UserRegisterForm(firstName, lastName, gender,
                    birthDate, userName, password, confirmPassword);
            vm.register(form, imageUri, this);
        });

        vm.getAuthResult().observe(this, authResult -> {
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
            if (authResult instanceof AuthResult.Success) {
                Toast.makeText(RegisterActivity.this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (authResult instanceof AuthResult.Error) {
                String errorMessage = ((AuthResult.Error) authResult).getMessage();
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker(EditText editBirthDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    // Format the date as MM/DD/YYYY
                    String date = String.format(Locale.US, "%02d/%02d/%d", month + 1, day, year);
                    editBirthDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void updateProfileInitial(String userName) {
        if (imageUri == null) {
            String initial = userName != null && !userName.isEmpty() ? userName.substring(0, 1).toUpperCase() : "?";
            profileInitial.setText(initial);
            int color = getColorForUser(userName);
            profileInitial.setBackground(createCircleDrawable(color));
            profileInitial.setVisibility(View.VISIBLE);
            profileImageView.setVisibility(View.GONE);
        }
    }

    private int getColorForUser(String userName) {
        return AvatarColorHelper.getColorForUser(this, userName);
    }

    private Drawable createCircleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            profileImageView.setVisibility(View.VISIBLE);
            profileInitial.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @Nullable int[] grantResults) {
        assert grantResults != null;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, getString(R.string.permission_denied_images), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
