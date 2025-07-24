package com.example.sunmail.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sunmail.model.Mail;
import com.example.sunmail.repository.MailRepository;
import com.example.sunmail.util.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

public class MailViewModel extends AndroidViewModel {
    private MailRepository mailRepository;
    private MutableLiveData<List<Mail>> mails = new MutableLiveData<>();
    private final MutableLiveData<String> deleteResult = new MutableLiveData<>();

    public MailViewModel(Application application) {
        super(application);
        mailRepository = new MailRepository(application);
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

    public LiveData<String> getDeleteResult() { return deleteResult; }

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

}
