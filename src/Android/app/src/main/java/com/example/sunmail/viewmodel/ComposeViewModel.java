package com.example.sunmail.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sunmail.model.AuthResult;
import com.example.sunmail.model.ComposeForm;
import com.example.sunmail.model.Mail;
import com.example.sunmail.repository.ComposeRepository;
import com.example.sunmail.util.SimpleCallback;

public class ComposeViewModel extends AndroidViewModel {
    private final ComposeRepository repository;
    private final MutableLiveData<AuthResult> sendResult = new MutableLiveData<>();
    private final MutableLiveData<String> currentDraftId = new MutableLiveData<>();
    private final MutableLiveData<Mail> currentDraft = new MutableLiveData<>();

    public ComposeViewModel(@NonNull Application app) {
        super(app);
        repository = new ComposeRepository(app);
    }

    public LiveData<AuthResult> getSendResult() {
        return sendResult;
    }

    public LiveData<String> getCurrentDraftId() {
        return currentDraftId;
    }

    public LiveData<Mail> getCurrentDraft() {
        return currentDraft;
    }

    public void createDraft(ComposeForm form) {
        repository.createDraft(form, new SimpleCallback<Mail>() {
            @Override
            public void onSuccess(Mail mail) {
                Log.d("ComposeViewModel", "Draft created successfully with ID: " + mail.getId());
                currentDraftId.postValue(mail.getId());
                currentDraft.postValue(mail);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ComposeViewModel", "Create draft failed: " + errorMessage);
                sendResult.postValue(new AuthResult.Error(errorMessage));
            }
        });
    }

    public void updateDraft(String draftId, ComposeForm form) {
        if (draftId == null) {
            createDraft(form);
            return;
        }

        repository.updateDraft(draftId, form, new SimpleCallback<Mail>() {
            @Override
            public void onSuccess(Mail mail) {
                Log.d("ComposeViewModel", "Draft updated successfully");
                currentDraft.postValue(mail);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ComposeViewModel", "Update draft failed: " + errorMessage);
                // Don't show error for auto-save failures to user
            }
        });
    }

    public void sendMail(String draftId) {
        if (draftId == null) {
            sendResult.postValue(new AuthResult.Error("No draft to send"));
            return;
        }

        repository.sendMail(draftId, new SimpleCallback<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                Log.d("ComposeViewModel", "Mail sent successfully");
                sendResult.postValue(new AuthResult.Success());
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ComposeViewModel", "Send mail failed: " + errorMessage);
                sendResult.postValue(new AuthResult.Error(errorMessage));
            }
        });
    }

    public void createDraftAndSend(ComposeForm form) {
        // First create the draft
        repository.createDraft(form, new SimpleCallback<Mail>() {
            @Override
            public void onSuccess(Mail draft) {
                Log.d("ComposeViewModel", "Draft created successfully, now sending...");
                // Draft created successfully, now send it
                sendMail(draft.getId());
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ComposeViewModel", "Create draft failed: " + errorMessage);
                sendResult.postValue(new AuthResult.Error(errorMessage));
            }
        });
    }
}
