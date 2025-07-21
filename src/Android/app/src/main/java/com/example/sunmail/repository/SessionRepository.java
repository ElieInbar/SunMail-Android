package com.example.sunmail.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SessionRepository {
    private final MutableLiveData<String> token = new MutableLiveData<>();

    public SessionRepository(Application app) {
        // For now, just store token in memory
        // TODO: Implement Room database for persistent storage
    }

    public void saveToken(String token) {
        this.token.postValue(token);
    }

    public LiveData<String> getSession() {
        return token;
    }

    public void clearSession() {
        token.postValue(null);
    }
}

//package com.example.sunmail.repository;
//
//import android.content.Context;
//
//import androidx.lifecycle.LiveData;
//
//import com.example.sunmail.model.AppDatabase;
//import com.example.sunmail.model.UserDao;
//import com.example.sunmail.model.UserEntity;
//
//import java.util.concurrent.Executors;
//
//public class SessionRepository {
//    private final UserDao dao;
//
//    public SessionRepository(Context context) {
//        AppDatabase db = AppDatabase.getDatabase(context);
//        this.dao = db.userSessionDao();
//    }
//
//    public void saveToken(String token) {
//        Executors.newSingleThreadExecutor().execute(() -> dao.insertSession(new UserEntity(token)));
//    }
//
//    public LiveData<UserEntity> getSession() {
//        return dao.getSession();
//    }
//
//    public void clearSession() {
//        Executors.newSingleThreadExecutor().execute(() -> dao.clearSession());
//    }
//}
