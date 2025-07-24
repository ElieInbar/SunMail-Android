package com.example.sunmail.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.sunmail.api.ApiClient;
import com.example.sunmail.api.MailApi;
import com.example.sunmail.model.Mail;
import com.example.sunmail.util.SimpleCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MailRepository {
    private MailApi mailApi;

    public MailRepository(Context context) {
        mailApi = ApiClient.get(context).create(MailApi.class);
    }

    //    public void fetchMails(MutableLiveData<List<Mail>> mailsLiveData) {
//        Call<List<Mail>> call = mailApi.getMails();
//        call.enqueue(new Callback<List<Mail>>() {
//            @Override
//            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    mailsLiveData.postValue(response.body());
//                } else {
//                    Log.e("MailRepository", "Erreur de réponse : " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Mail>> call, Throwable t) {
//                Log.e("MailRepository", "Erreur réseau", t);
//            }
//        });
//    }
    public void fetchMails(MutableLiveData<List<Mail>> mailsLiveData, String label) {
        Call<List<Mail>> call = mailApi.getMails(label);
        call.enqueue(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mailsLiveData.postValue(response.body());
                } else {
                    Log.e("MailRepository", "Erreur de réponse : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                Log.e("MailRepository", "Erreur réseau", t);
            }
        });
    }

    public void deleteMail(String mailId, SimpleCallback<Void> callback) {
        mailApi.deleteMail(mailId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Suppression échouée (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }


}
