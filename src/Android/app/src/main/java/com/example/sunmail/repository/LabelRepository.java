// repository/LabelRepository.java
package com.example.sunmail.repository;

import android.content.Context;

import com.example.sunmail.api.ApiClient;
import com.example.sunmail.api.LabelApi;
import com.example.sunmail.model.Label;
import com.example.sunmail.model.LabelRequest;

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
        labelApi.getLabels().enqueue(callback);
    }

    public void updateLabel(String id, String newName, Callback<Void> callback) {
        LabelRequest request = new LabelRequest(newName);
        labelApi.updateLabel(id, request).enqueue(callback);
    }

    public void deleteLabel(String id, Callback<Void> callback) {
        labelApi.deleteLabel(id).enqueue(callback);
    }


}
