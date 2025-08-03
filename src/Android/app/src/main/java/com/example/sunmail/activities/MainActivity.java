package com.example.sunmail.activities; // Package declaration

import android.content.Intent; // Import Intent class to launch activities
import android.os.Bundle; // Import Bundle to pass data between activities
import android.view.View;

import androidx.appcompat.app.AppCompatActivity; // Import base class for activities
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sunmail.R;

public class MainActivity extends AppCompatActivity { // Main activity class declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the activity layout

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View rootLayout = findViewById(R.id.main); // Get the root view of the layout

        rootLayout.setOnClickListener(v -> { // Set a click listener on the root view
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}
