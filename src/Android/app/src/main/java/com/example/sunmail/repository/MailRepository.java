package com.example.sunmail.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sunmail.api.ApiClient;
import com.example.sunmail.api.MailApi;
import com.example.sunmail.model.Label;
import com.example.sunmail.model.LabelIdRequest;
import com.example.sunmail.model.Mail;
import com.example.sunmail.model.ToBody;
import com.example.sunmail.util.SimpleCallback;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MailRepository {
    private MailApi mailApi;

    public MailRepository(Context context) {
        mailApi = ApiClient.get(context).create(MailApi.class);
    }
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

    public void markMailAsRead(String mailId, String label, Mail mail, SimpleCallback<Void> callback) {
        mailApi.markMailAsRead(mailId, label, new ToBody(mail.getReceiver())).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("PATCH", "Réponse PATCH: code=" + response.code());
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Erreur code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getLabelsForMail(String mailId, MutableLiveData<List<Label>> labelsLiveData) {
        mailApi.getLabelsForMail(mailId).enqueue(new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    labelsLiveData.postValue(response.body());
                } else {
                    Log.e("MailRepository", "Erreur de récupération des labels. Code = " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                Log.e("MailRepository", "Erreur réseau (labels)", t);
            }
        });
    }


    public void addLabelToMail(String mailId, String labelId, SimpleCallback<Void> callback) {
        LabelIdRequest request = new LabelIdRequest(labelId);
        mailApi.addLabelToMail(mailId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Erreur: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    public void removeLabelFromMail(String mailId, String labelId, SimpleCallback<Void> callback) {
        mailApi.removeLabelFromMail(mailId, labelId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

}
