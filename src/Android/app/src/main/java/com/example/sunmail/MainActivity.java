package com.example.sunmail; // Package declaration

import android.content.Intent; // Import Intent class to launch activities
import android.os.Bundle; // Import Bundle to pass data between activities
import android.view.View;

import androidx.activity.EdgeToEdge; // Import for edge-to-edge screen handling
import androidx.appcompat.app.AppCompatActivity; // Import base class for activities
import androidx.core.graphics.Insets; // Import for handling system margins
import androidx.core.view.ViewCompat; // Import for view compatibility
import androidx.core.view.WindowInsetsCompat; // Import for window insets handling

public class MainActivity extends AppCompatActivity { // Main activity class declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the activity layout

        View rootLayout = findViewById(R.id.main); // Get the root view of the layout

        rootLayout.setOnClickListener(v -> { // Set a click listener on the root view
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}
