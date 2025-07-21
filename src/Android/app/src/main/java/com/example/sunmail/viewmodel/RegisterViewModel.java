package com.example.sunmail.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.sunmail.model.AuthResult;
import com.example.sunmail.util.Resource;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.util.SimpleCallback;
import com.example.sunmail.repository.SessionRepository;


public class RegisterViewModel extends AndroidViewModel {
    private final AuthRepository repo;
    private final MutableLiveData<AuthResult> authResult  = new MutableLiveData<>();
    private final SessionRepository sessionRepository;



    public RegisterViewModel(@NonNull Application app) {
        super(app);
        repo = new AuthRepository(app);
        sessionRepository = new SessionRepository(app);

    }

    public void saveToken(String token) {
        sessionRepository.saveToken(token);
    }
    public LiveData<AuthResult> getAuthResult() {
        return authResult;
    }
    public void register(UserRegisterForm form) {
        if (!form.isValid()) {
            authResult.postValue(new AuthResult.Error("Please fill all required fields and ensure passwords match"));
            return;
        }

        repo.register(form, new SimpleCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                // TODO - Save user data to room if needed
                Log.d("RegisterViewModel", "Register success, token: " + data);
//                saveToken(token); // שמור את הטוקן מיד אחרי ההרשמה
                authResult.postValue(new AuthResult.Success());
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("RegisterViewModel", "Registration failed: " + errorMessage);
                authResult.postValue(new AuthResult.Error(errorMessage));
            }
        });
    }
}
//    public void register(UserRegisterForm f, Context ctx) {
//        boolean valid = true;
//        firstNameError.setValue("");
//        lastNameError.setValue("");
//        userNameError.setValue("");
//        emailError.setValue("");
//        passwordError.setValue("");
//        confirmPasswordError.setValue("");
//        genderError.setValue("");
//        birthDateError.setValue("");
//
//        if (f.firstName == null || f.firstName.isEmpty()) {
//            firstNameError.setValue("שדה חובה"); valid = false;
//        }
//        if (f.lastName == null || f.lastName.isEmpty()) {
//            lastNameError.setValue("שדה חובה"); valid = false;
//        }
//        if (f.userName == null || f.userName.isEmpty()) {
//            userNameError.setValue("שדה חובה"); valid = false;
//        }
//        if (f.email == null || f.email.isEmpty()) {
//            emailError.setValue("שדה חובה"); valid = false;
//        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(f.email).matches()) {
//            emailError.setValue("אימייל לא תקין"); valid = false;
//        }
//        if (f.password == null || f.password.isEmpty()) {
//            passwordError.setValue("שדה חובה"); valid = false;
//        } else if (f.password.length() < 8) {
//            passwordError.setValue("סיסמה חייבת להיות לפחות 8 תווים"); valid = false;
//        }
//        if (f.confirmPassword == null || f.confirmPassword.isEmpty()) {
//            confirmPasswordError.setValue("שדה חובה"); valid = false;
//        } else if (!f.password.equals(f.confirmPassword)) {
//            confirmPasswordError.setValue("הסיסמאות לא תואמות"); valid = false;
//        }
//        if (f.gender == null || f.gender.isEmpty()) {
//            genderError.setValue("שדה חובה"); valid = false;
//        }
//        if (f.birthDate == null || f.birthDate.isEmpty()) {
//            birthDateError.setValue("שדה חובה"); valid = false;
//        }
//        if (!valid) {
//            result.setValue(Resource.error("יש למלא את כל השדות כנדרש"));
//            return;
//        }
//        repo.register(f, ctx).observeForever(result::setValue);
//    }

//    public LiveData<Resource<Void>> getResult() { return result; }
//    public LiveData<String> getFirstNameError() { return firstNameError; }
//    public LiveData<String> getLastNameError() { return lastNameError; }
//    public LiveData<String> getUserNameError() { return userNameError; }
//    public LiveData<String> getEmailError() { return emailError; }
//    public LiveData<String> getPasswordError() { return passwordError; }
//    public LiveData<String> getConfirmPasswordError() { return confirmPasswordError; }
//    public LiveData<String> getGenderError() { return genderError; }
//    public LiveData<String> getBirthDateError() { return birthDateError; }
//}