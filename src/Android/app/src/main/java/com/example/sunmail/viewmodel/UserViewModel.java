package com.example.sunmail.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sunmail.model.User;
import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.util.SimpleCallback;

import java.util.List;

public class UserViewModel extends ViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<List<User>> users = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public UserViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<User>> getUsers() { return users; }

    public void fetchAllUsers() {
        repository.getAllUsers(new SimpleCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                users.postValue(data);
            }
            @Override
            public void onError(String errorMsg) {
                error.postValue(errorMsg);
            }
        });
    }
}