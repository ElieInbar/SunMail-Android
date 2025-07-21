package com.example.sunmail.api;

import com.example.sunmail.model.LoginRequest;
import com.example.sunmail.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AuthService {
    @POST("tokens")
    Call<Void> login(@Body LoginRequest body);

    @Multipart
    @POST("users")
    Call<Void> register(
            @Part("first_name") RequestBody firstName,
            @Part("last_name") RequestBody lastName,
            @Part("gender") RequestBody gender,
            @Part("birthDate") RequestBody birthDate,
            @Part("userName") RequestBody userName,
            @Part("password") RequestBody password,
            @Part("confirmPassword") RequestBody confirmPassword,
            @Part MultipartBody.Part profilePicture
    );
    @GET("users/by-username/{userName}")
    Call<User> getUserByUserName(@Path("userName") String userName);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") String id);

    @GET("users")
    Call<List<User>> getAllUsers();
}