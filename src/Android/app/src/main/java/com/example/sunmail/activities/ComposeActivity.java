<<<<<<<< HEAD:src/Android/app/src/main/java/com/example/sunmail/activity/ComposeActivity.java
package com.example.sunmail.activity;
========
package com.example.sunmail.activities;
>>>>>>>> origin/main:src/Android/app/src/main/java/com/example/sunmail/activities/ComposeActivity.java

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sunmail.R;

// Activity for composing a new email
public class ComposeActivity extends AppCompatActivity {
    // Declaration of UI views
    private ImageButton backButton;
    private ImageButton sendButton;
    private EditText toField;
    private EditText subjectField;
    private EditText bodyField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables Edge-to-Edge mode for the UI
        setContentView(R.layout.activity_compose); // Sets the activity layout

        initViews();      // Initializes the views
        setBackButton();  // Configures the back button
        setSendButton();  // Configures the send button
    }

    // Method to bind views to layout elements
    private void initViews() {
        backButton = findViewById(R.id.back_button);
        sendButton = findViewById(R.id.send_button);
        toField = findViewById(R.id.to_field);
        subjectField = findViewById(R.id.subject_field);
        bodyField = findViewById(R.id.body_field);
    }

    // Configures the behavior of the back button
    private void setBackButton() {
        backButton.setOnClickListener(v -> onBackPressed());
    }

    // Configures the behavior of the send button
    private void setSendButton() {
        sendButton.setOnClickListener(v -> {
            // Email sending logic here
            Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show();
            onBackPressed(); // Return to the previous screen after sending
        });
    }
}
