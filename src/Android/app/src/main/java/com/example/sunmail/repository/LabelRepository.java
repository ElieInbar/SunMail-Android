// repository/LabelRepository.java
package com.example.sunmail.repository;

import android.content.Context;
import android.util.Log;

import com.example.sunmail.api.ApiClient;
import com.example.sunmail.api.LabelApi;
import com.example.sunmail.model.Label;
import com.example.sunmail.model.LabelRequest;
import com.example.sunmail.util.SimpleCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabelRepository {
    private final LabelApi labelApi;

    public LabelRepository(Context context) {
        labelApi = ApiClient.get(context).create(LabelApi.class);
    }

    public void createLabel(String name, Callback<Void> callback) {
        LabelRequest request = new LabelRequest(name);
        labelApi.createLabel(request).enqueue(callback);
    }

    public void getLabels(Callback<List<Label>> callback) {
        labelApi.getLabels().enqueue(new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Label label : response.body()) {
                        Log.d("LabelRepository", "Label received: " + label.getName() + ", userId=" + label.getUserId());
                    }
                    callback.onResponse(call, response);
                } else {
                    Log.e("LabelRepository", "Response failed: " + response.code());
                    callback.onResponse(call, response); // Still pass response
                }
            }

            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                Log.e("LabelRepository", "Network error", t);
                callback.onFailure(call, t);
            }
        });
    }



    public void updateLabel(String id, String newName, Callback<Void> callback) {
        LabelRequest request = new LabelRequest(newName);
        labelApi.updateLabel(id, request).enqueue(callback);
    }

    public void deleteLabel(String id, Callback<Void> callback) {
        labelApi.deleteLabel(id).enqueue(callback);
    }

    public void getLabelByName(String name, Callback<Label> callback) {
        labelApi.getLabelByName(name).enqueue(callback);
    }

}
