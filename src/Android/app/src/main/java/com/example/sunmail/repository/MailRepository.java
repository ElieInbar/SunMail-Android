package com.example.sunmail.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sunmail.model.Mail;
import com.example.sunmail.api.ApiClient;
import com.example.sunmail.api.MailApi;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MailRepository {
    private MailApi mailApi;

    public MailRepository() {
        mailApi = ApiClient.get().create(MailApi.class);
    }

    public LiveData<List<Mail>> getMails() {
        MutableLiveData<List<Mail>> liveData = new MutableLiveData<>();
        mailApi.getMails().enqueue(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                Log.d("MailRepo", "URL appelée : " + call.request().url());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("MailRepo", "Réponse reçue, size = " + response.body().size());
                    liveData.postValue(response.body());
                } else {
                    Log.e("MailRepo", "Réponse KO : code " + response.code());
                    liveData.postValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                Log.e("MailRepo", "Erreur réseau", t);
                liveData.postValue(Collections.emptyList());
            }
        });
        return liveData;
    }




=======
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
>>>>>>> origin/main
}
