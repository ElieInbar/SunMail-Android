// MailRepository.java
package com.example.sunmail.repository;
import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.sunmail.api.ApiClient;
//import com.example.sunmail.api.MailService;
//import com.example.sunmail.model.Mail;
import com.example.sunmail.model.UserSessionEntity;
import com.example.sunmail.util.SimpleCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MailRepository {
    private final SessionRepository sessionRepository;

    public MailRepository(Application app) {
        sessionRepository = new SessionRepository(app);
    }

//    public void getInbox(LifecycleOwner owner, SimpleCallback<List<Mail>> callback) {
//        sessionRepository.getSession().observe(owner, session -> {
//            if (session == null || session.token == null) {
//                callback.onError("No token found");
//                return;
//            }
//
//            MailService service = ApiClient.getWithToken(session.token).create(MailService.class);
//            service.getInbox().enqueue(new Callback<>() {
//                @Override
//                public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        callback.onSuccess(response.body());
//                    } else {
//                        callback.onError("Failed to get inbox");
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<Mail>> call, Throwable t) {
//                    callback.onError(t.getMessage());
//                }
//            });
//        });
//    }
}
