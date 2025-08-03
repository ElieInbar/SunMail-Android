// viewmodel/LabelViewModel.java
package com.example.sunmail.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sunmail.model.Label;
import com.example.sunmail.repository.LabelRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabelViewModel extends AndroidViewModel {
    private final LabelRepository labelRepository;
    private final MutableLiveData<String> labelCreationStatus = new MutableLiveData<>();
    private final MutableLiveData<List<Label>> labels = new MutableLiveData<>();

    public LabelViewModel(@NonNull Application application) {
        super(application);
        labelRepository = new LabelRepository(application.getApplicationContext());
    }

    public void createLabel(String name, Callback<Void> callback) {
        labelRepository.createLabel(name, callback);
    }
    public LiveData<String> getLabelCreationStatus() {
        return labelCreationStatus;
    }

    public void createLabel(String name) {
        labelRepository.createLabel(name, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    labelCreationStatus.postValue("success");
                    fetchLabels();
                } else {
                    labelCreationStatus.postValue("error:" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                labelCreationStatus.postValue("network:" + t.getMessage());
            }
        });
    }

    public LiveData<List<Label>> getLabels() {
        return labels;
    }

    public void fetchLabels() {
        labelRepository.getLabels(new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                if (response.isSuccessful()) {
                    labels.postValue(response.body());
                } else {
                    labels.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                labels.postValue(new ArrayList<>());
            }
        });
    }

    public void updateLabel(String id, String newName) {
        labelRepository.updateLabel(id, newName, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchLabels();
                }else{
                    Toast.makeText(getApplication(), "Error while editing label", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplication(), "Network failure : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteLabel(String id) {
        labelRepository.deleteLabel(id, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchLabels();
                } else {
                    Toast.makeText(getApplication(), "Error while deleting label", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplication(), "Network Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
