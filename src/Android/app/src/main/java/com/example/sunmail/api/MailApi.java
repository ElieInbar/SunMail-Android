package com.example.sunmail.api;

import com.example.sunmail.model.Label;
import com.example.sunmail.model.LabelIdRequest;
import com.example.sunmail.model.Mail;
import com.example.sunmail.model.ToBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

// MailApi.java
public interface MailApi {
    @GET("mails/label/{label}")
    Call<List<Mail>> getMails(@Path("label") String label);

    @DELETE("mails/{id}")
    Call<Void> deleteMail(@Path("id") String mailId);

    @PATCH("mails/{id}/read/{labelName}")
    Call<Void> markMailAsRead(
            @Path("id") String mailId,
            @Path("labelName") String labelName,
            @Body ToBody body
    );

    @GET("mails/{id}/labels")
    Call<List<Label>> getLabelsForMail(@Path("id") String mailId);

    @POST("mails/{id}/labels")
    Call<Void> addLabelToMail(
            @Path("id") String mailId,
            @Body LabelIdRequest request
    );

    @DELETE("mails/{mailId}/labels/{labelId}")
    Call<Void> removeLabelFromMail(@Path("mailId") String mailId, @Path("labelId") String labelId);

    @GET("mails/search/{query}")
    Call<List<Mail>> searchMails(@Path("query") String query);

}
