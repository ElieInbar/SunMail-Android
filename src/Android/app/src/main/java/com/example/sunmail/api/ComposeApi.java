package com.example.sunmail.api;

import com.example.sunmail.model.ComposeForm;
import com.example.sunmail.model.Mail;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ComposeApi {
    @POST("mails")
    Call<Mail> createDraft(@Body ComposeForm form);

    @PATCH("mails/{id}")
    Call<Mail> updateDraft(@Path("id") String id, @Body ComposeForm form);

    @POST("mails/{id}/send")
    Call<Void> sendMail(@Path("id") String id);
}
