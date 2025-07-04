package com.example.sunmail.repository;
import android.content.Context;

import com.example.sunmail.model.Mail;
import com.example.sunmail.network.ApiClient;
import com.example.sunmail.network.MailApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MailRepository {
    private MailApi mailApi;

    public MailRepository(Context context) {
        mailApi = ApiClient.getClient(context).create(MailApi.class);
    }

    public void getMails(Callback<List<Mail>> callback) {
        Call<List<Mail>> call = mailApi.getMails();
        call.enqueue(callback);
    }

    // MailRepository.java
    public void login(String email, String password, Callback<Void> callback) {
        // Crée une map/objet pour le body JSON (si besoin)
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        Call<Void> call = mailApi.login(body);
        call.enqueue(callback); // Callback pour traiter la réponse (succès ou erreur)
    }

}
