package com.example.sunmail.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.sunmail.R;
import com.example.sunmail.model.Label;
import com.example.sunmail.model.Mail;
import com.example.sunmail.util.AvatarColorHelper;
import com.example.sunmail.util.FlowLayout;
import com.example.sunmail.util.ThemeManager;
import com.example.sunmail.viewmodel.LabelViewModel;
import com.example.sunmail.viewmodel.MailViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewMailActivity extends AppCompatActivity {
    private Mail mail;
    private MailViewModel mailViewModel;
    private LabelViewModel labelViewModel;
    private String senderName; // Store sender name for reply/forward

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme before setting content view
        int savedThemeMode = ThemeManager.getThemeMode(this);
        ThemeManager.applyTheme(savedThemeMode);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable Edge-to-Edge mode like ComposeActivity
        setContentView(R.layout.activity_view_mail);

        // Retrieve the Mail object passed via Intent
        mail = (Mail) getIntent().getSerializableExtra("mail");
        mailViewModel = new ViewModelProvider(this).get(MailViewModel.class);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

        // Initialize UI components
        TextView subject = findViewById(R.id.text_subject);
        TextView sender = findViewById(R.id.text_sender);
        TextView body = findViewById(R.id.text_body);
        TextView senderMail = findViewById(R.id.text_sender_email);
        TextView avatar = findViewById(R.id.text_avatar);
        FlowLayout labelContainer = findViewById(R.id.label_container);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.viewmail_toolbar);
        setSupportActionBar(toolbar);
        // Show back arrow (Up button)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(""); // Or set a title if you want

        // Handle back navigation
        toolbar.setNavigationOnClickListener(v -> finish());

        // Retrieve sender's username from Intent
        senderName = (String) getIntent().getSerializableExtra("senderName");
        String sunmail = senderName.concat("@sunmail.com");

        // Populate UI with mail data if available
        if (mail != null) {
            subject.setText(mail.getSubject());
            sender.setText(senderName);
            body.setText(mail.getBody());
            senderMail.setText(sunmail);
            avatar.setText(senderName.isEmpty() ? "?" : senderName.substring(0, 1).toUpperCase());
            avatar.setBackground(createCircleDrawable(getColorForUser(senderName)));
        }

        // Setup Reply and Forward buttons
        setupReplyForwardButtons();

        // Observe the result of mail deletion
        mailViewModel.getDeleteResult().observe(this, result -> {
            if ("success".equals(result)) {
                Toast.makeText(this, getString(R.string.mail_deleted), Toast.LENGTH_SHORT).show();
                finish(); // Return to the mail list
            } else if (result != null && !result.equals("")) {
                Toast.makeText(this, getString(R.string.error_colon, result), Toast.LENGTH_LONG).show();
            }
        });

        mailViewModel.getLabelAddStatus().observe(this, status -> {
            if ("success".equals(status)) {
                Toast.makeText(this, getString(R.string.label_added), Toast.LENGTH_SHORT).show();
                mailViewModel.loadLabelsForMail(mail.getId());
            } else if (status != null) {
                Toast.makeText(this, getString(R.string.error_adding_label, status), Toast.LENGTH_SHORT).show();
            }
        });

        mailViewModel.getLabelRemoveStatus().observe(this, status -> {
            if ("success".equals(status)) {
                Toast.makeText(this, getString(R.string.label_removed), Toast.LENGTH_SHORT).show();
                mailViewModel.loadLabelsForMail(mail.getId());
            } else if (status != null) {
                Toast.makeText(this, getString(R.string.error_removing_label, status), Toast.LENGTH_SHORT).show();
            }
        });

        mailViewModel.loadLabelsForMail(mail.getId());
        mailViewModel.getMailLabels().observe(this, labels -> {
            labelContainer.removeAllViews();
            if (labels != null && !labels.isEmpty()) {
                for (Label label : labels) {
                    String name = label.getName().toLowerCase();
                    if (name.equals("all") || name.equals("sent")) continue;

                    TextView chip = new TextView(this);
                    chip.setText(label.getName());
                    chip.setTextSize(13);
                    // Use theme-appropriate text color instead of hardcoded white
                    chip.setTextColor(ContextCompat.getColor(this, R.color.white));
                    chip.setBackgroundResource(R.drawable.label_chip_background);
                    chip.setPadding(20, 10, 20, 10);
                    chip.setClickable(true);

                    chip.setOnClickListener(v -> {
                        new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.remove_label_question))
                                .setMessage(getString(R.string.remove_label_message, label.getName()))
                                .setPositiveButton(getString(R.string.yes), (d, w) -> {
                                    mailViewModel.removeLabelFromMail(mail.getId(), label.getId());
                                })
                                .setNegativeButton(getString(R.string.cancel), null)
                                .show();
                    });

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMarginEnd(16);
                    chip.setLayoutParams(params);
                    labelContainer.addView(chip);
                }
            } else {
                TextView empty = new TextView(this);
                empty.setText(getString(R.string.no_labels));
                labelContainer.addView(empty);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_add_label) {
            showAddLabelDialog();
            return true;
        }

        if (itemId == R.id.action_toggle_star) {
            mailViewModel.addSystemLabelFast(mail.getId(), "starred");
            return true;
        }

        if (itemId == R.id.action_toggle_important) {
            mailViewModel.addSystemLabelFast(mail.getId(), "important");
            return true;
        }

        if (itemId == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete))
                    .setMessage(getString(R.string.delete_this_mail))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> mailViewModel.deleteMail(mail.getId()))
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
            return true;
        } else if (item.getItemId() == R.id.action_edit_draft) {
            handleEditDraft();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if present
        getMenuInflater().inflate(R.menu.viewmail_menu, menu);

        // Show/hide edit draft icon based on whether this is a draft
        MenuItem editDraftItem = menu.findItem(R.id.action_edit_draft);
        if (editDraftItem != null) {
            editDraftItem.setVisible(isDraftMail());
        }

        return true;
    }

    /**
     * Setup Reply and Forward buttons
     */
    private void setupReplyForwardButtons() {
        Button replyButton = findViewById(R.id.btn_reply);
        Button forwardButton = findViewById(R.id.btn_forward);

        // Hide Reply/Forward buttons for drafts
        if (isDraftMail()) {
            replyButton.setVisibility(View.GONE);
            forwardButton.setVisibility(View.GONE);
        } else {
            replyButton.setOnClickListener(v -> handleReply());
            forwardButton.setOnClickListener(v -> handleForward());
        }
    }

    /**
     * Handle Reply action
     */
    private void handleReply() {
        if (mail == null) {
            Toast.makeText(this, getString(R.string.error_no_mail_to_reply), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ComposeActivity.class);
        intent.putExtra("ACTION_TYPE", "REPLY");
        intent.putExtra("ORIGINAL_MAIL", mail);
        intent.putExtra("SENDER_NAME", senderName);
        startActivity(intent);
    }

    /**
     * Handle Forward action
     */
    private void handleForward() {
        if (mail == null) {
            Toast.makeText(this, getString(R.string.error_no_mail_to_forward), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ComposeActivity.class);
        intent.putExtra("ACTION_TYPE", "FORWARD");
        intent.putExtra("ORIGINAL_MAIL", mail);
        intent.putExtra("SENDER_NAME", senderName);
        startActivity(intent);
    }

    /**
     * Check if the current mail is a draft
     */
    private boolean isDraftMail() {
        String currentLabel = getIntent().getStringExtra("currentLabel");
        return mail != null && "drafts".equals(currentLabel);
    }

    /**
     * Handle Edit Draft action
     */
    private void handleEditDraft() {
        Intent intent = new Intent(this, ComposeActivity.class);
        intent.putExtra("ACTION_TYPE", "EDIT_DRAFT");
        intent.putExtra("DRAFT_MAIL", mail);

        // Pass receiver name if available (for drafts)
        String receiverName = getIntent().getStringExtra("receiverName");
        if (receiverName != null) {
            intent.putExtra("RECEIVER_NAME", receiverName);
        }

        startActivity(intent);
        finish(); // Close ViewMailActivity
    }

    // Same color generation logic as in RegisterActivity and MailAdapter
    private int getColorForUser(String userName) {
        return AvatarColorHelper.getColorForUser(this, userName);
    }

    private Drawable createCircleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    private void showAddLabelDialog() {
        mailViewModel.loadLabelsForMail(mail.getId());
        labelViewModel.fetchLabels();

        LiveData<List<Label>> allLabelsLive = labelViewModel.getLabels();
        LiveData<List<Label>> mailLabelsLive = mailViewModel.getMailLabels();
        MediatorLiveData<Boolean> mediator = new MediatorLiveData<>();

        mediator.addSource(allLabelsLive, labels -> {
            if (labels != null && mailLabelsLive.getValue() != null) mediator.setValue(true);
        });

        mediator.addSource(mailLabelsLive, labels -> {
            if (labels != null && allLabelsLive.getValue() != null) mediator.setValue(true);
        });

        mediator.observe(this, ready -> {
            tryShowDialog();
            mediator.removeObservers(this);
        });
    }

    private void tryShowDialog() {
        List<Label> allLabels = labelViewModel.getLabels().getValue();
        List<Label> assignedLabels = mailViewModel.getMailLabels().getValue();

        if (allLabels == null || assignedLabels == null) return;

        List<Label> filtered = new ArrayList<>();
        for (Label label : allLabels) {
            boolean alreadyAssigned = false;
            for (Label assigned : assignedLabels) {
                if (label.getId().equals(assigned.getId())) {
                    alreadyAssigned = true;
                    break;
                }
            }
            if (!alreadyAssigned) filtered.add(label);
        }

        if (filtered.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_label_to_add), Toast.LENGTH_SHORT).show();
            return;
        }

        String[] labelNames = new String[filtered.size()];
        for (int i = 0; i < filtered.size(); i++) {
            labelNames[i] = filtered.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_a_label))
                .setItems(labelNames, (dialog, which) -> {
                    String labelId = filtered.get(which).getId();
                    mailViewModel.addLabelToMail(mail.getId(), labelId);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
}
