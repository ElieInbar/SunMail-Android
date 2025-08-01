package com.example.sunmail.activities;

import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.viewmodel.HomeViewModel;

import android.widget.TextView;
import android.view.View;
import android.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sunmail.R;
import com.example.sunmail.adapter.MailAdapter;
import com.example.sunmail.model.Mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.example.sunmail.viewmodel.MailViewModel;
import com.example.sunmail.viewmodel.UserViewModelFactory;
import com.google.android.material.navigation.NavigationView;
import com.example.sunmail.viewmodel.UserViewModel;


public class HomeActivity extends AppCompatActivity {
    // Declaration of main views
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private NavigationView navigationView;
    private ImageButton composeButton;
    private MailViewModel mailViewModel;
    private MailAdapter mailAdapter;
    private RecyclerView recyclerView;
    private HomeViewModel homeViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserViewModel userViewModel;
    private String myUserId = null;
    private String username = null;
    private String label = "inbox";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews(); // Initialize main views
        setupDrawer(); // Setup navigation drawer
        setupComposeButton(); // Setup compose button
        setupLogout(); // Setup logout functionality

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.emails_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mailViewModel = new ViewModelProvider(this).get(MailViewModel.class);
        mailAdapter = new MailAdapter(new ArrayList<>(), mailViewModel, label);
        recyclerView.setAdapter(mailAdapter);

        // Initialize user view model with repository
        AuthRepository repository = new AuthRepository(getApplication());
        UserViewModelFactory userFactory = new UserViewModelFactory(repository);
        userViewModel = new ViewModelProvider(this, userFactory).get(UserViewModel.class);

        // Observe user map and load mails when ready
        userViewModel.getUserMap().observe(this, userMap -> {
            mailAdapter.setUserMap(userMap);
            mailViewModel.loadMails(label);
        });
        userViewModel.fetchAllUsers();

        // Observe mails and update adapter with filtered and sorted mails
        mailViewModel.getMails().observe(this, mails -> {
            if (myUserId == null || mails == null) {
                mailAdapter.setMailList(new ArrayList<>()); // No mails to display
                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            List<Mail> filteredMails = new ArrayList<>();
            for (Mail mail : mails) {
                // Filter mails for the current user
                if (myUserId.equals(mail.getReceiver())) {
                    filteredMails.add(mail);
                }
            }
            // Sort mails by date (most recent first)
            Collections.sort(filteredMails, (m1, m2) -> {
                Date d1 = m1.getCreatedAt();
                Date d2 = m2.getCreatedAt();
                return d2.compareTo(d1);
            });
            mailAdapter.setMailList(filteredMails);
            swipeRefreshLayout.setRefreshing(false);
        });

        // Pull-to-refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mailViewModel.loadMails(label);
        });

        swipeRefreshLayout.setRefreshing(true);
        mailViewModel.loadMails(label);

        // Observe user session info
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getSession().observe(this, session -> {
            if (session != null) {
                myUserId = session.userId;
                username = session.userName;
                TextView profileButton = findViewById(R.id.profile_button);
                if (profileButton != null) {
                    profileButton.setText(username == null || username.isEmpty() ? "?" : username.substring(0, 1).toUpperCase());
                }
                String info = "userId=" + session.userId +
                        ", userName=" + session.userName +
                        ", email=" + session.email +
                        ", profilePicture=" + session.profilePicture +
                        ", token=" + session.token;
                Log.d("UserSessionInfo", info);
                Toast.makeText(this, info, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mailViewModel.loadMails(label); // Reload mails when activity resumes
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
            String message = "";
            String selectedLabel = label; // By default keep the current label

            // Handle navigation menu selections
            if (id == R.id.nav_inbox) {
                selectedLabel = "inbox";
                message = "Inbox selected";
            } else if (id == R.id.nav_starred) {
                selectedLabel = "starred";
                message = "Starred selected";
            } else if (id == R.id.nav_important) {
                selectedLabel = "important";
                message = "Important selected";
            } else if (id == R.id.nav_sent) {
                selectedLabel = "sent";
                message = "Sent selected";
            } else if (id == R.id.nav_drafts) {
                selectedLabel = "drafts";
                message = "Drafts selected";
            } else if (id == R.id.nav_all_mail) {
                selectedLabel = "all";
                message = "All Mail selected";
            } else if (id == R.id.nav_spam) {
                selectedLabel = "spam";
                message = "Spam selected";
            } else if (id == R.id.nav_trash) {
                selectedLabel = "trash";
                message = "Trash selected";
            } else if (id == R.id.nav_theme) {
                selectedLabel = "inbox";
                message = "Theme changed";
            } else if (id == R.id.nav_help) {
                selectedLabel = "inbox";
                message = "Help information";
            } else {
                message = "Other option selected";
            }

            label = selectedLabel;

            mailAdapter.setCurrentLabel(label);
            mailViewModel.loadMails(label);

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

    // Setup the compose button and its click action
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

    // Setup logout functionality and observe session changes
    private void setupLogout() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        TextView profileButton = findViewById(R.id.profile_button);
        if (profileButton != null) {
            profileButton.setOnClickListener(this::showProfileMenu);
        }
        homeViewModel.getSession().observe(this, session -> {
            // If session is invalid, redirect to login
            if (session == null || session.token == null || session.token.isEmpty()) {
                Intent intent = new Intent(HomeActivity.this, com.example.sunmail.activities.LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    // Show profile menu with logout option
    private void showProfileMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                showLogoutDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    // Show confirmation dialog for logout
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("logout")
                .setMessage("are you sure you want to logout?")
                .setPositiveButton("yes", (dialog, which) -> homeViewModel.logout())
                .setNegativeButton("no", null)
                .show();
    }

}
