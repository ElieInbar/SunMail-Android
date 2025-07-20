package com.example.sunmail.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sunmail.util.Resource;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.util.SimpleCallback;

public class RegisterViewModel extends AndroidViewModel {
    private final AuthRepository repo;
    private final MutableLiveData<Resource<Void>> registerState  = new MutableLiveData<>();
//    private final MutableLiveData<String> firstNameError = new MutableLiveData<>("");
//    private final MutableLiveData<String> lastNameError = new MutableLiveData<>("");
//    private final MutableLiveData<String> userNameError = new MutableLiveData<>("");
//    private final MutableLiveData<String> emailError = new MutableLiveData<>("");
//    private final MutableLiveData<String> passwordError = new MutableLiveData<>("");
//    private final MutableLiveData<String> confirmPasswordError = new MutableLiveData<>("");
//    private final MutableLiveData<String> genderError = new MutableLiveData<>("");
//    private final MutableLiveData<String> birthDateError = new MutableLiveData<>("");

    public RegisterViewModel(@NonNull Application app) {
        super(app);
        repo = new AuthRepository(app);
    }

    public LiveData<Resource<Void>> getRegisterState() {
        return registerState;
    }

    public void register(UserRegisterForm form, Context ctx) {
        registerState.setValue(Resource.loading());

        repo.register(form, ctx, new SimpleCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                registerState.postValue(Resource.success(null));
            }

            @Override
            public void onError(String msg) {
                registerState.postValue(Resource.error(msg));
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