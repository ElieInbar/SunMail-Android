package com.example.sunmail.repository;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import com.example.sunmail.api.ApiClient;
import com.example.sunmail.api.AuthService;
import com.example.sunmail.model.LoginRequest;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.util.SimpleCallback;
import com.example.sunmail.model.User;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.sunmail.util.FileUtils;

public class AuthRepository {
    private static final String TAG = "AuthRepository";

    private final AuthService api;

    public AuthRepository(Application app) {
        api = ApiClient.get(app).create(AuthService.class);
    }

    public void login(String email, String password, SimpleCallback<String> callback) {
        Call<Void> call = api.login(new LoginRequest(email, password));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful() && response.headers().get("Set-Cookie") != null) {
                    String cookie = response.headers().get("Set-Cookie");
                    //TODO - Save the token in Room
                    Log.i("AuthRepository", "Login successful, cookie: " + cookie);
                    callback.onSuccess(cookie);
                } else {
                    callback.onError("Login failed: invalid credentials");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Login failed: " + t.getMessage(), t); // Log failure
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void register(UserRegisterForm form, Uri imageUri, Context context, SimpleCallback<Void> callback) {
        RequestBody firstName = RequestBody.create(form.getFirstName(), MediaType.parse("text/plain"));
        RequestBody lastName = RequestBody.create(form.getLastName(), MediaType.parse("text/plain"));
        RequestBody gender = RequestBody.create(form.getGender(), MediaType.parse("text/plain"));
        RequestBody birthDate = RequestBody.create(form.getBirthDate(), MediaType.parse("text/plain"));
        RequestBody userName = RequestBody.create(form.getUserName(), MediaType.parse("text/plain"));
        RequestBody password = RequestBody.create(form.getPassword(), MediaType.parse("text/plain"));
        RequestBody confirmPassword = RequestBody.create(form.getConfirmPassword(), MediaType.parse("text/plain"));

    MultipartBody.Part profilePicturePart = null;
        if (imageUri != null) {
            try {
                File file = FileUtils.copyToTempJpeg(context, imageUri);
                RequestBody reqFile = RequestBody.create(file, okhttp3.MediaType.parse("image/jpeg"));
                profilePicturePart = MultipartBody.Part.createFormData("profilePicture", file.getName(), reqFile);
            } catch (IOException e) {
                callback.onError("Failed to read image: " + e.getMessage());
                return;
            }
        }

        Call<Void> call = api.register(
                firstName, lastName, gender, birthDate, userName, password, confirmPassword, profilePicturePart
        );
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i("AuthRepository", "Registration successful");
                    callback.onSuccess(null);
                } else {
                    callback.onError("Registration failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Registration failed: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getUserByUserName(String userName, SimpleCallback<User> callback) {
        Call<User> call = api.getUserByUserName(userName);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch user info");
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getUserById(String id, SimpleCallback<User> callback) {
        Call<User> call = api.getUserById(id);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch user info by id");
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getAllUsers(SimpleCallback<List<User>> callback) {
        Call<List<User>> call = api.getAllUsers();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch users");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
