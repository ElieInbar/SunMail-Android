package com.example.sunmail.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sunmail.model.Label;
import com.example.sunmail.model.Mail;
import com.example.sunmail.repository.LabelRepository;
import com.example.sunmail.repository.MailRepository;
import com.example.sunmail.util.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Context;
import android.content.SharedPreferences;

public class MailViewModel extends AndroidViewModel {
    private MailRepository mailRepository;
    private MutableLiveData<List<Mail>> mails = new MutableLiveData<>();
    private final MutableLiveData<String> deleteResult = new MutableLiveData<>();
    private MutableLiveData<List<Label>> mailLabels = new MutableLiveData<>();
    private final MutableLiveData<String> labelRemoveStatus = new MutableLiveData<>();
    private final MutableLiveData<String> labelAddStatus = new MutableLiveData<>();
    private final LabelRepository labelRepository;

    public MailViewModel(Application application) {
        super(application);
        mailRepository = new MailRepository(application);
        this.labelRepository = new LabelRepository(application.getApplicationContext());

    }


    public LiveData<List<Mail>> getMails() {
        return mails;
    }

    public void loadMails(String label) {
        mailRepository.fetchMails(mails, label);
    }

    public LiveData<List<Mail>> getInboxMails(String myUserId) {
        MutableLiveData<List<Mail>> inboxMails = new MutableLiveData<>();
        getMails().observeForever(allMails -> {
            List<Mail> filtered = new ArrayList<>();
            for (Mail mail : allMails) {
                if (myUserId.equals(mail.getReceiver())) {
                    filtered.add(mail);
                }
            }
            inboxMails.setValue(filtered);
        });
        return inboxMails;
    }

    public LiveData<String> getDeleteResult() {
        return deleteResult;
    }

    public void deleteMail(String mailId) {
        mailRepository.deleteMail(mailId, new SimpleCallback<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                deleteResult.postValue("success");
            }

            @Override
            public void onError(String errorMsg) {
                deleteResult.postValue(errorMsg);
            }
        });
    }

    public void markMailAsRead(String mailId, String label, Mail mail) {
        mailRepository.markMailAsRead(mailId, label, mail, new SimpleCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                loadMails(label); // à activer si tu veux refresh direct
            }

            @Override
            public void onError(String errorMsg) {
                // Optionnel: Log, Toast
            }
        });
    }

    public LiveData<List<Label>> getMailLabels() {
        return mailLabels;
    }

    public void loadLabelsForMail(String mailId) {
        mailRepository.getLabelsForMail(mailId, mailLabels);
    }

    public void addLabelToMail(String mailId, String labelId) {
        mailRepository.addLabelToMail(mailId, labelId, new SimpleCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                labelAddStatus.postValue("success");
                loadLabelsForMail(mailId);
            }

            @Override
            public void onError(String errorMsg) {
                labelAddStatus.postValue(errorMsg);
            }
        });
    }


    public void removeLabelFromMail(String mailId, String labelId) {
        mailRepository.removeLabelFromMail(mailId, labelId, new SimpleCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                labelRemoveStatus.postValue("success");
            }

            @Override
            public void onError(String errorMsg) {
                labelRemoveStatus.postValue(errorMsg);
            }
        });
    }

    public LiveData<String> getLabelRemoveStatus() {
        return labelRemoveStatus;
    }
    public LiveData<String> getLabelAddStatus() {
        return labelAddStatus;
    }
    public void removeLabelFromMail(String mailId, String labelId, SimpleCallback<Void> callback) {
        mailRepository.removeLabelFromMail(mailId, labelId, callback);
    }
    public void addLabelToMail(String mailId, String labelId, SimpleCallback<Void> callback) {
        mailRepository.addLabelToMail(mailId, labelId, callback);
    }

    public void addSystemLabelFast(String mailId, String labelName) {
        labelRepository.getLabelByName(labelName, new Callback<Label>() {
            @Override
            public void onResponse(Call<Label> call, Response<Label> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String labelId = response.body().getId();
                    addLabelToMail(mailId, labelId, new SimpleCallback<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            labelAddStatus.postValue("success");
                            loadLabelsForMail(mailId);
                        }

                        @Override
                        public void onError(String errorMsg) {
                            labelAddStatus.postValue("Erreur ajout label: " + errorMsg);
                        }
                    });
                } else {
                    labelAddStatus.postValue("Label not found");
                }
            }

            @Override
            public void onFailure(Call<Label> call, Throwable t) {
                labelAddStatus.postValue("Erreur réseau: " + t.getMessage());
            }
        });
    }

}
