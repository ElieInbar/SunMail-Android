package com.example.sunmail.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.sunmail.R;
import com.example.sunmail.model.Mail;
import com.example.sunmail.util.ThemeManager;
import com.example.sunmail.viewmodel.MailViewModel;


public class ViewMailActivity extends AppCompatActivity {
    private Mail mail;
    private MailViewModel mailViewModel;
    private String senderName; // Store sender name for reply/forward

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

        // Initialize UI components
        TextView subject = findViewById(R.id.text_subject);
        TextView sender = findViewById(R.id.text_sender);
        TextView body = findViewById(R.id.text_body);
        TextView senderMail = findViewById(R.id.text_sender_email);
        TextView avatar = findViewById(R.id.text_avatar);

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

        // Retrieve the Mail object again (redundant, can be removed)
        mail = (Mail) getIntent().getSerializableExtra("mail");
        // Initialize the ViewModel
        mailViewModel = new ViewModelProvider(this).get(MailViewModel.class);

        // Observe the result of mail deletion
        mailViewModel.getDeleteResult().observe(this, result -> {
            if ("success".equals(result)) {
                Toast.makeText(this, "Mail deleted", Toast.LENGTH_SHORT).show();
                finish(); // Return to the mail list
            } else if (result != null && !result.equals("")) {
                Toast.makeText(this, "Error: " + result, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            // Show confirmation dialog before deleting mail
            new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Delete this mail?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mailViewModel.deleteMail(mail.getId());
                    })
                    .setNegativeButton("No", null)
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
            Toast.makeText(this, "Error: No mail to reply to", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Error: No mail to forward", Toast.LENGTH_SHORT).show();
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
        int[] colors = {
                0xFFE57373, 0xFFF06292, 0xFFBA68C8, 0xFF64B5F6, 0xFF4DB6AC,
                0xFFFFB74D, 0xFFA1887F, 0xFF90A4AE, 0xFF81C784, 0xFFDCE775
        };
        int hash = userName != null ? Math.abs(userName.hashCode()) : 0;
        return colors[hash % colors.length];
    }

    private Drawable createCircleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }
}
