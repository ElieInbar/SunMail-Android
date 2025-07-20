package com.example.sunmail.api;

import com.example.sunmail.model.AuthResult;
import com.example.sunmail.model.LoginRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AuthService {

    // 1. Register – multipart בגלל תמונה (profilePicture)
    @Multipart
    @POST("users")
    Call<Void> register(
            @Part MultipartBody.Part profilePicture,
            @Part("first_name") RequestBody firstName,
            @Part("last_name") RequestBody lastName,
            @Part("userName") RequestBody userName,
            @Part("email") RequestBody email,
            @Part("gender") RequestBody gender,
            @Part("birthDate") RequestBody birthDate,
            @Part("password") RequestBody password,
            @Part("confirmPassword") RequestBody confirmPassword
    );

    // 2. Login – JSON פשוט
    @POST("tokens")
    Call<Void> login(@Body LoginRequest body);
}
