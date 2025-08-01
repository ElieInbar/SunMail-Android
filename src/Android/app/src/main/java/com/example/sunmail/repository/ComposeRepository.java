package com.example.sunmail.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sunmail.api.ApiClient;
import com.example.sunmail.api.ComposeApi;
import com.example.sunmail.model.ComposeForm;
import com.example.sunmail.model.Mail;
import com.example.sunmail.util.SimpleCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComposeRepository {
    private static final String TAG = "ComposeRepository";

    private final ComposeApi api;

    public ComposeRepository(Application app) {
        api = ApiClient.get(app).create(ComposeApi.class);
    }

    public void createDraft(ComposeForm form, SimpleCallback<Mail> callback) {
        Call<Mail> call = api.createDraft(form);
        call.enqueue(new Callback<Mail>() {
            @Override
            public void onResponse(@NonNull Call<Mail> call, @NonNull Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Draft created successfully");
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to create draft: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mail> call, @NonNull Throwable t) {
                Log.e(TAG, "Create draft failed: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updateDraft(String draftId, ComposeForm form, SimpleCallback<Mail> callback) {
        Call<Mail> call = api.updateDraft(draftId, form);
        call.enqueue(new Callback<Mail>() {
            @Override
            public void onResponse(@NonNull Call<Mail> call, @NonNull Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Draft updated successfully");
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update draft: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mail> call, @NonNull Throwable t) {
                Log.e(TAG, "Update draft failed: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void sendMail(String draftId, SimpleCallback<Void> callback) {
        Call<Void> call = api.sendMail(draftId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Mail sent successfully");
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to send mail: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Send mail failed: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
