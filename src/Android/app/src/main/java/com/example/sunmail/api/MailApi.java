package com.example.sunmail.api;

import com.example.sunmail.model.Mail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

// MailApi.java
public interface MailApi {
    @GET("mails/label/{label}")
    Call<List<Mail>> getMails(@Path("label") String label);

    @DELETE("mails/{id}")
    Call<Void> deleteMail(@Path("id") String mailId);
}
