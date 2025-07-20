package com.example.sunmail.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sunmail.api.ApiClient;
import com.example.sunmail.api.AuthService;
import com.example.sunmail.model.LoginRequest;
import com.example.sunmail.model.UserRegisterForm;
import com.example.sunmail.util.Resource;
import com.example.sunmail.util.SimpleCallback;
import com.example.sunmail.model.AuthResult;
//import com.example.sunmail.api.model.User;
//import com.example.sunmail.db.AppDatabase;
//import com.example.sunmail.db.UserDao;
//import com.example.sunmail.db.UserEntityMapper;
import com.example.sunmail.util.SimpleCallback;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AuthRepository {

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

    public void register(UserRegisterForm form, Context ctx, SimpleCallback<Void> callback) {
        MultipartBody.Part imagePart = form.toImagePart(ctx);
        Map<String, RequestBody> fields = form.toRequestMap(ctx);

        api.register(
                imagePart,
                fields.get("first_name"),
                fields.get("last_name"),
                fields.get("userName"),
                fields.get("email"),
                fields.get("gender"),
                fields.get("birthDate"),
                fields.get("password"),
                fields.get("confirmPassword")
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Registration failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /* ---------- Login ---------- */
    public void login(String email, String password, SimpleCallback<String> callback) {
        Call<Void> call = api.login(new LoginRequest(email, password));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    String cookie = response.headers().get("Set-Cookie");
                    Log.i("AuthRepository", "Login successful, cookie: " + cookie);
                    callback.onSuccess(cookie);
                } else {
                    callback.onError("Login failed: invalid credentials");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /* ---------- Helpers ---------- */
//    private void storeUser(User user) {
//        io.execute(() ->
//                userDao.save(new UserEntityMapper().map(user)));
//    }
}
