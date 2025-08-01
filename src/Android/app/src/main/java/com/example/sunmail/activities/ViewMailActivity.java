package com.example.sunmail.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sunmail.viewmodel.MailViewModel;

import com.example.sunmail.R;
import com.example.sunmail.model.Mail;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;


public class ViewMailActivity extends AppCompatActivity {
    private Mail mail;
    private MailViewModel mailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        String username = (String) getIntent().getSerializableExtra("senderName");
        String sunmail = username.concat("@sunmail.com");

        // Populate UI with mail data if available
        if (mail != null) {
            subject.setText(mail.getSubject());
            sender.setText(username);
            body.setText(mail.getBody());
            senderMail.setText(sunmail);
            avatar.setText(username.isEmpty() ? "?" : username.substring(0, 1).toUpperCase());
        }

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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if present
        getMenuInflater().inflate(R.menu.viewmail_menu, menu);
        return true;
    }
}
