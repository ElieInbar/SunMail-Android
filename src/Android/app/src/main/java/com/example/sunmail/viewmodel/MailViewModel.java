package com.example.sunmail.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sunmail.model.Mail;
import com.example.sunmail.repository.MailRepository;

import java.util.ArrayList;
import java.util.List;

public class MailViewModel extends AndroidViewModel {
    private MailRepository mailRepository;
    private MutableLiveData<List<Mail>> mails = new MutableLiveData<>();

    public MailViewModel(Application application) {
        super(application);
        mailRepository = new MailRepository(application);
    }

    public LiveData<List<Mail>> getMails() {
        return mails;
    }

    public void loadMails() {
        mailRepository.fetchMails(mails);
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

}
