package com.example.sunmail.network;

import com.example.sunmail.model.Mail;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

// MailApi.java
public interface MailApi {
    @GET("api/mails")
    Call<List<Mail>> getMails();
    @POST("api/tokens")
    Call<Void> login(@Body Map<String, String> body);
}
