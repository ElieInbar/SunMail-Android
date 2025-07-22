package com.example.sunmail.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.sunmail.model.UserSessionEntity;
import com.example.sunmail.repository.SessionRepository;

public class HomeViewModel extends AndroidViewModel {
    private final SessionRepository sessionRepository;

    public HomeViewModel(@NonNull Application app) {
        super(app);
        sessionRepository = new SessionRepository(app);
    }

    public LiveData<UserSessionEntity> getSession() {
        return sessionRepository.getSession();
    }

    public void logout() {
        sessionRepository.clearSession();
    }
} 