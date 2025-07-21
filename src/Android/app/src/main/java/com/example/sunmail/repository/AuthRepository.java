package com.example.sunmail.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.example.sunmail.api.ApiClient;
import com.example.sunmail.api.AuthService;
import com.example.sunmail.model.LoginRequest;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.util.SimpleCallback;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AuthRepository {
    private static final String TAG = "AuthRepository";

    private final AuthService api;
//    private final UserDao userDao;
    private final Executor io = Executors.newSingleThreadExecutor();

    public AuthRepository(Application app) {
        api = ApiClient.get().create(AuthService.class);
//        userDao = Room.databaseBuilder(app,
//                        AppDatabase.class, "sunmail.db")
//                .build()
//                .userDao();
    }

    /* ---------- Login ---------- */
    public void login(String email, String password, SimpleCallback<String> callback) {
        Call<Void> call = api.login(new LoginRequest(email, password));
        call.enqueue(new Callback<Void>() {
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

    public void register(UserRegisterForm form, SimpleCallback<Void> callback) {
        // Convert form data to RequestBody
        RequestBody firstName = RequestBody.create(MediaType.parse("text/plain"), form.getFirstName());
        RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), form.getLastName());
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), form.getGender());
        RequestBody birthDate = RequestBody.create(MediaType.parse("text/plain"), form.getBirthDate());
        RequestBody userName = RequestBody.create(MediaType.parse("text/plain"), form.getUserName());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), form.getPassword());
        RequestBody confirmPassword = RequestBody.create(MediaType.parse("text/plain"), form.getConfirmPassword());

        Call<Void> call = api.register(firstName, lastName, gender, birthDate, userName, password, confirmPassword);
        call.enqueue(new Callback<Void>() {
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



    /* ---------- Register ---------- */
//    public LiveData<Resource<Void>> register(UserRegisterForm form, android.content.Context ctx) {
//        MutableLiveData<Resource<Void>> live = new MutableLiveData<>(Resource.loading());
//        MultipartBody.Part imagePart = form.toImagePart(ctx);
//        Map<String,RequestBody> fields = form.toRequestMap();
//
//        api.register(imagePart,
//                        fields.get("first_name"),
//                        fields.get("last_name"),
//                        fields.get("userName"),
//                        fields.get("email"),
//                        fields.get("gender"),
//                        fields.get("birthDate"),
//                        fields.get("password"),
//                        fields.get("confirmPassword"))
//                .enqueue(new SimpleCallback<Void>(live));
//        return live;
//    }

//    public void register(UserRegisterForm form, Context ctx, SimpleCallback<String> callback) {
//        MultipartBody.Part imagePart = form.toImagePart(ctx);
//        Map<String, RequestBody> fields = form.toRequestMap(ctx);
//
//        api.register(
//                fields.get("first_name"),
//                fields.get("last_name"),
//                fields.get("userName"),
//                fields.get("email"),
//                fields.get("gender"),
//                fields.get("birthDate"),
//                fields.get("password"),
//                fields.get("confirmPassword"),
//                imagePart
//        ).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                if (response.isSuccessful()) {
//                    callback.onSuccess(null);
//                } else {
//                    callback.onError("Registration failed: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                callback.onError("Network error: " + t.getMessage());
//            }
//        });
//    }

//    public void register(UserRegisterForm form, Context ctx, SimpleCallback<String> callback) {
//        MultipartBody.Part imagePart = form.toImagePart(ctx);
//        Map<String, RequestBody> fields = form.toRequestMap(ctx);
//
//        Call<Void> call = api.register(imagePart, fields);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    String token = extractTokenFromCookie(response);
//                    if (token != null) {
//                        callback.onSuccess(token);
//                    } else {
//                        callback.onError("Registration succeeded but token is missing");
//                    }
//                } else {
//                    callback.onError("Register failed: " + response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                callback.onError("Register error: " + t.getMessage());
//            }
//        });
//    }
//
//    // Utility method to extract the JWT from the Set-Cookie header
//    private String extractTokenFromCookie(Response<?> response) {
//        String header = response.headers().get("Set-Cookie");
//        if (header == null) return null;
//
//        for (String cookie : header.split(";")) {
//            if (cookie.trim().startsWith("token=")) {
//                return cookie.trim().substring("token=".length());
//            }
//        }
//        return null;
//    }
//
//    private RequestBody createPartFromString(String value) {
//        return RequestBody.create(value, MediaType.parse("text/plain"));
//    }

    /* ---------- Helpers ---------- */
//    private void storeUser(User user) {
//        io.execute(() ->
//                userDao.save(new UserEntityMapper().map(user)));
//    }
}
