package com.example.sunmail;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {
    // Declaration of main views
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navigationView;
    private ImageButton composeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Sets the activity layout

        initViews();    // Initializes the views
        setupDrawer();  // Configures the navigation drawer
        setupComposeButton(); // Configures the compose button
    }

    // Method to bind layout views to variables
    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);      // Navigation drawer
        menuButton = findViewById(R.id.menu_button);          // Button to open the drawer
        navigationView = findViewById(R.id.navigation_view);  // Navigation view (menu)
    }

    // Method to configure the navigation drawer and its actions
    private void setupDrawer() {
        // Opens the drawer when the menu button is clicked
        menuButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        // Handles selection of navigation menu items
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            String message;

            // Shows a different message depending on the selected item
            if (id == R.id.nav_inbox) {
                message = "Inbox selected";
            } else if (id == R.id.nav_starred) {
                message = "Starred selected";
            } else if (id == R.id.nav_important) {
                message = "Important selected";
            } else if (id == R.id.nav_sent) {
                message = "Sent selected";
            } else if (id == R.id.nav_drafts) {
                message = "Drafts selected";
            } else if (id == R.id.nav_all_mail) {
                message = "All Mail selected";
            } else if (id == R.id.nav_spam) {
                message = "Spam selected";
            } else if (id == R.id.nav_trash) {
                message = "Trash selected";
            } else if (id == R.id.nav_theme) {
                message = "Theme changed";
            } else if (id == R.id.nav_help) {
                message = "Help information";
            } else {
                message = "Other option selected";
            }

            // Displays a Toast with the corresponding message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawers(); // Closes the drawer after selection
            return true;
        });

    }

    // Handles the back button behavior
    @Override
    public void onBackPressed() {
        // If the drawer is open, close it instead of exiting the activity
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupComposeButton() {
        composeButton = findViewById(R.id.btn_compose); // Gets the compose button

        // Sets the action to perform when the button is clicked
        composeButton.setOnClickListener(v -> {
            // Shows a Toast message to indicate the button was clicked
            Toast.makeText(getApplicationContext(), "Compose button clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, ComposeActivity.class);
            startActivity(intent);
        });
    }

}
