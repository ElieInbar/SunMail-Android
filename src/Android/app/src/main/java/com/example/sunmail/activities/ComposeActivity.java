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
import com.example.sunmail.model.Mail;
import com.example.sunmail.util.ComposeUtils;
import com.example.sunmail.viewmodel.ComposeViewModel;

// Activity for composing a new email
public class ComposeActivity extends AppCompatActivity {
    // Action type constants
    public static final String ACTION_REPLY = "REPLY";
    public static final String ACTION_FORWARD = "FORWARD";
    public static final String ACTION_EDIT_DRAFT = "EDIT_DRAFT";

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

    // Reply/Forward data
    private String actionType;
    private Mail originalMail;
    private String senderName;

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

            // Handle sending: create draft if needed, then send
            ComposeForm form = getCurrentFormData();
            if (currentDraftId != null) {
                // Draft exists, update it and send
                viewModel.updateDraft(currentDraftId, form);
                // Small delay to ensure update completes before sending
                autoSaveHandler.postDelayed(() -> viewModel.sendMail(currentDraftId), 100);
            } else {
                // No draft exists, create one and then send it automatically
                viewModel.createDraftAndSend(form);
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

    // Setup text watchers (no auto-save during typing)
    private void setupAutoSave() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Just cancel any pending auto-save callbacks
                // No auto-save during typing - only save on back action
                autoSaveHandler.removeCallbacks(autoSaveRunnable);
            }
        };

        toField.addTextChangedListener(textWatcher);
        subjectField.addTextChangedListener(textWatcher);
        bodyField.addTextChangedListener(textWatcher);
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
            // Check for Reply/Forward action
            actionType = intent.getStringExtra("ACTION_TYPE");
            originalMail = (Mail) intent.getSerializableExtra("ORIGINAL_MAIL");
            senderName = intent.getStringExtra("SENDER_NAME");

            if (ACTION_REPLY.equals(actionType) && originalMail != null) {
                handleReplyIntent();
            } else if (ACTION_FORWARD.equals(actionType) && originalMail != null) {
                handleForwardIntent();
            } else if (ACTION_EDIT_DRAFT.equals(actionType)) {
                handleEditDraftIntent();
            } else {
                // Handle regular compose with pre-filled data
                String to = intent.getStringExtra("to");
                String subject = intent.getStringExtra("subject");
                String body = intent.getStringExtra("body");

                if (to != null) toField.setText(to);
                if (subject != null) subjectField.setText(subject);
                if (body != null) bodyField.setText(body);
            }

            // No longer create draft immediately - only save on back
        }
    }

    /**
     * Handle Reply intent - pre-fill fields for reply
     */
    private void handleReplyIntent() {
        if (originalMail == null) return;

        // Set recipient (sender of original mail)
        String senderEmail = senderName + "@sunmail.com";
        toField.setText(senderEmail);

        // Set subject with "Re: " prefix
        String replySubject = ComposeUtils.formatReplySubject(originalMail.getSubject());
        subjectField.setText(replySubject);

        // Set body with quoted original message
        String replyBody = ComposeUtils.formatReplyBody(originalMail, senderName);
        bodyField.setText(replyBody);

        // Position cursor at the beginning for user to type
        bodyField.setSelection(0);
    }

    /**
     * Handle Forward intent - pre-fill fields for forward
     */
    private void handleForwardIntent() {
        if (originalMail == null) return;

        // Leave recipient empty (user will fill)
        toField.setText("");

        // Set subject with "Fwd: " prefix
        String forwardSubject = ComposeUtils.formatForwardSubject(originalMail.getSubject());
        subjectField.setText(forwardSubject);

        // Set body with complete original message
        String forwardBody = ComposeUtils.formatForwardBody(originalMail, senderName, "You"); // TODO: Get actual recipient name
        bodyField.setText(forwardBody);

        // Position cursor at the beginning for user to type
        bodyField.setSelection(0);

        // Focus on 'To' field since it's empty
        toField.requestFocus();
    }

    /**
     * Handle Edit Draft intent - pre-fill fields with draft data
     */
    private void handleEditDraftIntent() {
        Intent intent = getIntent();
        Mail draftMail = (Mail) intent.getSerializableExtra("DRAFT_MAIL");

        if (draftMail == null) return;

        // IMPORTANT: Set the current draft ID BEFORE filling the fields
        // to prevent creating a new draft when TextWatchers trigger
        currentDraftId = draftMail.getId();

        // Pre-fill all fields with draft data
        String receiverText = "";
        String receiverName = intent.getStringExtra("RECEIVER_NAME");

        if (receiverName != null && !receiverName.isEmpty()) {
            // Use the provided receiver name (already processed by MailAdapter)
            receiverText = receiverName;
        } else if (draftMail.getReceiver() != null && !draftMail.getReceiver().isEmpty()) {
            // Fallback: use receiver ID as-is (should rarely happen)
            receiverText = draftMail.getReceiver();
        }

        toField.setText(receiverText);
        subjectField.setText(draftMail.getSubject() != null ? draftMail.getSubject() : "");
        bodyField.setText(draftMail.getBody() != null ? draftMail.getBody() : "");

        // Focus on the first empty field or body
        if (toField.getText().toString().trim().isEmpty()) {
            toField.requestFocus();
        } else if (subjectField.getText().toString().trim().isEmpty()) {
            subjectField.requestFocus();
        } else {
            bodyField.requestFocus();
            // Position cursor at the end
            bodyField.setSelection(bodyField.getText().length());
        }
    }

    /**
     * Handle back button press - save draft if there's content
     */
    @Override
    public void onBackPressed() {
        saveOnBack();
        super.onBackPressed();
    }

    /**
     * Save or update draft when user goes back
     */
    private void saveOnBack() {
        if (!hasContent()) {
            // No content, no need to save
            return;
        }

        ComposeForm form = getCurrentFormData();

        if (currentDraftId == null) {
            // Create new draft
            viewModel.createDraft(form);
        } else {
            // Update existing draft
            viewModel.updateDraft(currentDraftId, form);
        }
    }
}
