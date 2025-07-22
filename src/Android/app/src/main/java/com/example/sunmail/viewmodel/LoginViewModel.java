package com.example.sunmail.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.app.Application;
import androidx.annotation.NonNull;

import com.example.sunmail.model.AuthResult;
import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.util.SimpleCallback;
import com.example.sunmail.repository.SessionRepository;
import androidx.lifecycle.Transformations;
public class LoginViewModel extends AndroidViewModel {
    private final AuthRepository repo;
    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();
    private final SessionRepository sessionRepository;

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
                session -> session != null && session.token != null && !session.token.isEmpty());
    }

    private String extractToken(String cookie) {
        if (cookie == null) return null;
        for (String part : cookie.split(";")) {
            if (part.trim().startsWith("token=")) {
                return part.trim().substring("token=".length());
            }
        }
        return null;
    }

    public void login(String userNameOrEmail, String password) {
        String email = userNameOrEmail.contains("@") ? userNameOrEmail : userNameOrEmail + "@sunmail.com";
        String userName = userNameOrEmail.contains("@") ? userNameOrEmail.split("@")[0] : userNameOrEmail;
        repo.login(email, password, new SimpleCallback<>() {
            @Override
            public void onSuccess(String data) {
                // TODO - Save cookie to room
                String token = extractToken(data);
                repo.getUserByUserName(userName, new SimpleCallback<>() {
                    @Override
                    public void onSuccess(com.example.sunmail.model.User user) {
                        sessionRepository.saveSession(token, user.getId(), user.getUserName(), user.getEmail(), user.getProfilePicture());
                        authResult.postValue(new AuthResult.Success());
                    }
                    @Override
                    public void onError(String errorMessage) {
                        authResult.postValue(new AuthResult.Error(errorMessage));
                    }
                });
            }
            @Override
            public void onError(String errorMessage) {
                authResult.postValue(new AuthResult.Error(errorMessage));
            }
        });
    }
}
