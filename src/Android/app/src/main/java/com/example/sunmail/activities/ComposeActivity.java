package com.example.sunmail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sunmail.R;
import com.example.sunmail.model.AuthResult;
import com.example.sunmail.model.ComposeForm;
import com.example.sunmail.viewmodel.ComposeViewModel;

// Activity for composing a new email
public class ComposeActivity extends AppCompatActivity {
    // Declaration of UI views
    private ImageButton backButton;
    private ImageButton sendButton;
    private EditText toField;
    private EditText subjectField;
    private EditText bodyField;
    private ProgressBar progressBar;

    // ViewModel and state
    private ComposeViewModel viewModel;
    private String currentDraftId = null;

    // Auto-save functionality
    private Handler autoSaveHandler = new Handler();
    private Runnable autoSaveRunnable = new Runnable() {
        @Override
        public void run() {
            autoSaveDraft();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables Edge-to-Edge mode for the UI
        setContentView(R.layout.activity_compose); // Sets the activity layout

        // Initialize ViewModel (like RegisterActivity)
        viewModel = new ViewModelProvider(this).get(ComposeViewModel.class);

        initViews();      // Initializes the views
        setBackButton();  // Configures the back button
        setSendButton();  // Configures the send button
        setupAutoSave();  // Setup auto-save functionality
        observeViewModel(); // Observe ViewModel results (like RegisterActivity)

        // Handle Intent data for Reply/Forward
        handleIntentData();
    }

    // Method to bind views to layout elements
    private void initViews() {
        backButton = findViewById(R.id.back_button);
        sendButton = findViewById(R.id.send_button);
        toField = findViewById(R.id.to_field);
        subjectField = findViewById(R.id.subject_field);
        bodyField = findViewById(R.id.body_field);
        progressBar = findViewById(R.id.progressBar);
    }

    // Configures the behavior of the back button
    private void setBackButton() {
        backButton.setOnClickListener(v -> onBackPressed());
    }

    // Configures the behavior of the send button
    private void setSendButton() {
        sendButton.setOnClickListener(v -> {
            // VALIDATION (same pattern as RegisterActivity)
            String to = toField.getText().toString().trim();
            String subject = subjectField.getText().toString().trim();
            String body = bodyField.getText().toString().trim();

            if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // LOADING STATE (same pattern as RegisterActivity)
            setLoadingState(true);

            // Cancel any pending auto-save to avoid conflicts
            autoSaveHandler.removeCallbacks(autoSaveRunnable);

            // Force update draft with current data before sending
            if (currentDraftId != null) {
                ComposeForm form = getCurrentFormData();
                viewModel.updateDraft(currentDraftId, form);
                // Small delay to ensure update completes before sending
                autoSaveHandler.postDelayed(() -> viewModel.sendMail(currentDraftId), 100);
            } else {
                Toast.makeText(this, "Error: No draft to send", Toast.LENGTH_SHORT).show();
                setLoadingState(false);
            }
        });
    }

    // Set loading state (like RegisterActivity)
    private void setLoadingState(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        sendButton.setEnabled(!loading);
        toField.setEnabled(!loading);
        subjectField.setEnabled(!loading);
        bodyField.setEnabled(!loading);
    }

    // Setup auto-save functionality
    private void setupAutoSave() {
        TextWatcher autoSaveWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Remove previous auto-save callback
                autoSaveHandler.removeCallbacks(autoSaveRunnable);

                // Create draft if it doesn't exist and there's content
                if (currentDraftId == null && hasContent()) {
                    createInitialDraft();
                } else if (currentDraftId != null) {
                    // Schedule auto-save in 2 seconds
                    autoSaveHandler.postDelayed(autoSaveRunnable, 2000);
                }
            }
        };

        toField.addTextChangedListener(autoSaveWatcher);
        subjectField.addTextChangedListener(autoSaveWatcher);
        bodyField.addTextChangedListener(autoSaveWatcher);
    }

    private boolean hasContent() {
        return !toField.getText().toString().trim().isEmpty() ||
               !subjectField.getText().toString().trim().isEmpty() ||
               !bodyField.getText().toString().trim().isEmpty();
    }

    private void createInitialDraft() {
        ComposeForm form = getCurrentFormData();
        viewModel.createDraft(form);
    }

    private void autoSaveDraft() {
        if (currentDraftId != null && hasContent()) {
            ComposeForm form = getCurrentFormData();
            viewModel.updateDraft(currentDraftId, form);
        }
    }

    private ComposeForm getCurrentFormData() {
        ComposeForm form = new ComposeForm(
            toField.getText().toString().trim(),
            subjectField.getText().toString().trim(),
            bodyField.getText().toString().trim()
        );
        // Set the current draft ID if available
        if (currentDraftId != null) {
            form.setDraftId(currentDraftId);
        }
        return form;
    }

    // Observe ViewModel results (same pattern as RegisterActivity)
    private void observeViewModel() {
        viewModel.getSendResult().observe(this, result -> {
            setLoadingState(false);

            if (result instanceof AuthResult.Success) {
                Toast.makeText(this, "Email sent successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Return to previous activity
            } else if (result instanceof AuthResult.Error) {
                String errorMessage = ((AuthResult.Error) result).getMessage();
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getCurrentDraftId().observe(this, draftId -> {
            if (draftId != null) {
                currentDraftId = draftId;
            }
        });
    }

    // Handle Intent data for Reply/Forward
    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String to = intent.getStringExtra("to");
            String subject = intent.getStringExtra("subject");
            String body = intent.getStringExtra("body");

            if (to != null) toField.setText(to);
            if (subject != null) subjectField.setText(subject);
            if (body != null) bodyField.setText(body);

            // If fields are pre-filled, create draft immediately
            if (hasContent()) {
                createInitialDraft();
            }
        }
    }
}
