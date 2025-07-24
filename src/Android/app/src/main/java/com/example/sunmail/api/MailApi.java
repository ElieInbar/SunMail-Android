package com.example.sunmail.api;

import com.example.sunmail.model.Mail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

// MailApi.java
public interface MailApi {
    @GET("mails")
    Call<List<Mail>> getMails();
}
