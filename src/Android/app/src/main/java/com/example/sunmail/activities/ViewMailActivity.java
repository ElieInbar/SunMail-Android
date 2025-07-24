package com.example.sunmail.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sunmail.R;
import com.example.sunmail.model.Mail;

public class ViewMailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mail);

        Mail mail = (Mail) getIntent().getSerializableExtra("mail");

        TextView subject = findViewById(R.id.text_subject);
        TextView sender = findViewById(R.id.text_sender);
        TextView body = findViewById(R.id.text_body);
        TextView senderMail = findViewById(R.id.text_sender_email);
        TextView avatar = findViewById(R.id.text_avatar);
        // Ajoute d’autres TextView si besoin

        String username = (String) getIntent().getSerializableExtra("senderName");
        String sunmail= username.concat("@sunmail.com");;
        if (mail != null) {
            subject.setText(mail.getSubject());
            sender.setText(username);
            body.setText(mail.getBody());
            senderMail.setText(sunmail);
            avatar.setText(username.isEmpty() ? "?" : username.substring(0, 1).toUpperCase());

        }
    }
}
