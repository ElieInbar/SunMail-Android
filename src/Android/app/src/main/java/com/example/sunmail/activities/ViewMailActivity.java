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

        mail = (Mail) getIntent().getSerializableExtra("mail");

        TextView subject = findViewById(R.id.text_subject);
        TextView sender = findViewById(R.id.text_sender);
        TextView body = findViewById(R.id.text_body);
        TextView senderMail = findViewById(R.id.text_sender_email);
        TextView avatar = findViewById(R.id.text_avatar);

        Toolbar toolbar = findViewById(R.id.viewmail_toolbar);
        setSupportActionBar(toolbar);
        // Affiche la flèche retour (Up)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(""); // Ou tu mets un titre si tu veux

        toolbar.setNavigationOnClickListener(v -> finish());

        String username = (String) getIntent().getSerializableExtra("senderName");
        String sunmail = username.concat("@sunmail.com");
        ;
        if (mail != null) {
            subject.setText(mail.getSubject());
            sender.setText(username);
            body.setText(mail.getBody());
            senderMail.setText(sunmail);
            avatar.setText(username.isEmpty() ? "?" : username.substring(0, 1).toUpperCase());

        }
        mail = (Mail) getIntent().getSerializableExtra("mail");
        mailViewModel = new ViewModelProvider(this).get(MailViewModel.class);

        // Gestion de la Toolbar flèche retour déjà en place

        // Observe la suppression
        mailViewModel.getDeleteResult().observe(this, result -> {
            if ("success".equals(result)) {
                Toast.makeText(this, "Mail supprimé", Toast.LENGTH_SHORT).show();
                finish(); // Revenir à la liste
            } else if (result != null && !result.equals("")) {
                Toast.makeText(this, "Erreur: " + result, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            // Confirmation
            new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Delete this Mail")
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
        getMenuInflater().inflate(R.menu.viewmail_menu, menu);
        return true;
    }
}
