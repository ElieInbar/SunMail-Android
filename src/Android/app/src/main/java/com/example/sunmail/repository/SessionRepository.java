package com.example.sunmail.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.sunmail.db.AppDatabase;
import com.example.sunmail.db.UserSessionDao;
import com.example.sunmail.model.UserSessionEntity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SessionRepository {
    private final UserSessionDao dao;
    private final LiveData<UserSessionEntity> sessionLiveData;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public SessionRepository(Application app) {
        AppDatabase db = Room.databaseBuilder(app, AppDatabase.class, "sunmail_db").build();
        dao = db.userSessionDao();
        sessionLiveData = dao.getSession();
    }

    public void saveSession(String token, String userId, String userName, String email, String profilePicture) {
        executor.execute(() -> dao.insert(new UserSessionEntity(token, userId, userName, email, profilePicture)));
    }

    public void saveToken(String token) {
        executor.execute(() -> dao.insert(new UserSessionEntity(token)));
    }

    public LiveData<UserSessionEntity> getSession() {
        return sessionLiveData;
    }

    public void clearSession() {
        executor.execute(dao::clear);
    }
}
