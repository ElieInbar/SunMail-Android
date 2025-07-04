package com.example.sunmail.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;
import android.app.Application;

import com.example.sunmail.model.Mail;
import com.example.sunmail.repository.MailRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MailViewModel extends AndroidViewModel {
    private MutableLiveData<List<Mail>> mails;
    private MailRepository repository;

    public MailViewModel(Application application) {
        super(application);
        repository = new MailRepository(application); // <--- Ici on passe un context valide
        mails = new MutableLiveData<>();
    }

    public LiveData<List<Mail>> getMails() {
        return mails;
    }

    public void fetchMails() {
        repository.getMails(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful()) {
                    mails.postValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                mails.postValue(new ArrayList<>());
            }
        });
    }

    public void login(String email, String password, Callback<Void> callback) {
        repository.login(email, password, callback);
    }

}
