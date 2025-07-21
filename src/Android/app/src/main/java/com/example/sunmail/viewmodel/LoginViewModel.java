package com.example.sunmail.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sunmail.model.AuthResult;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.util.Resource;
import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.util.SimpleCallback;
//import com.example.sunmail.util.ValidationUtils;
import com.example.sunmail.repository.SessionRepository;
import com.example.sunmail.model.UserEntity;
import androidx.lifecycle.Transformations;
public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository repo;
    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();
    private final SessionRepository sessionRepository;

//
//    public final MutableLiveData<String> password = new MutableLiveData<>("");
//    public LiveData<Resource<Void>> result;


    public LoginViewModel(@NonNull Application app) {
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

    public LiveData<Boolean> isLoggedIn() {
        return Transformations.map(sessionRepository.getSession(),
                token -> token != null && !token.isEmpty());
    }

    public void login(String email, String password) {
        repo.login(email, password, new SimpleCallback<String>() {
            @Override
            public void onSuccess(String data) {
                // TODO - Save cookie to room
                saveToken(data);
                Log.d("LoginViewModel", "Login successful, token: " + data);
                authResult.postValue(new AuthResult.Success());
            }

            @Override
            public void onError(String errorMessage) {
                authResult.postValue(new AuthResult.Error(errorMessage));
            }
        });
    }

//    public void register(UserRegisterForm form) {
//        repo.register(form, new SimpleCallback<Void>() {
//            @Override
//            public void onSuccess(Void data) {
//                authResult.postValue(new AuthResult.Success());
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                authResult.postValue(new AuthResult.Error(errorMessage));
//            }
//        });
//    }
}
