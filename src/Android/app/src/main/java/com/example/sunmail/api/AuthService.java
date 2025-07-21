package com.example.sunmail.api;

import com.example.sunmail.model.LoginRequest;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.model.User;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Header;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

import java.util.Map;
public interface AuthService {
    @POST("tokens")
    Call<Void> login(@Body LoginRequest body);

    @Multipart
    @POST("users")
    Call<Void> register(@Part("first_name") RequestBody firstName,
                        @Part("last_name") RequestBody lastName,
                        @Part("gender") RequestBody gender,
                        @Part("birthDate") RequestBody birthDate,
                        @Part("userName") RequestBody userName,
                        @Part("password") RequestBody password,
                        @Part("confirmPassword") RequestBody confirmPassword);

    @GET("users/by-username/{userName}")
    Call<User> getUserByUserName(@Path("userName") String userName);
}

//public interface AuthService {

    // 1. Register – multipart בגלל תמונה (profilePicture)
//    @Multipart
//    @POST("users")
//    Call<Void> register(
//            @Part("first_name") RequestBody firstName,
//            @Part("last_name") RequestBody lastName,
//            @Part("userName") RequestBody userName,
//            @Part("email") RequestBody email,
//            @Part("gender") RequestBody gender,
//            @Part("birthDate") RequestBody birthDate,
//            @Part("password") RequestBody password,
//            @Part("confirmPassword") RequestBody confirmPassword,
//            @Part MultipartBody.Part profilePicture
//    );

//    @Multipart
//    @POST("/api/users")
//    Call<Void> register(@Part MultipartBody.Part profilePicture, @PartMap Map<String, RequestBody> form);
//    // 2. Login – JSON פשוט
//    @POST("tokens")
//    Call<Void> login(@Body LoginRequest body);
//
//    // GET user details by ID
////    @GET("/api/users/{id}")
////    Call<User> getUser(@Header("Authorization") String token, @Path("id") String userId);
//
//    // GET to retrieve userId and isAdmin based on token
//    @GET("/api/tokens")
//    Call<Map<String, Object>> getTokenInfo(@Header("Authorization") String authHeader);
//
//
//}
