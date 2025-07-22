package com.example.sunmail.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sunmail.model.AuthResult;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.util.SimpleCallback;
import com.example.sunmail.repository.SessionRepository;

import android.net.Uri;
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
    public void register(UserRegisterForm form, Uri imageUri, Context context) {
        if (!form.isValid()) {
            authResult.postValue(new AuthResult.Error("Please fill all required fields and ensure passwords match"));
            return;
        }
        repo.register(form, imageUri, context, new SimpleCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Log.d("RegisterViewModel", "Registration successful");
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