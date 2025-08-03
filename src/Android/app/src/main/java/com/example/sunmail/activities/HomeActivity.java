package com.example.sunmail.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sunmail.R;
import com.example.sunmail.adapter.MailAdapter;
import com.example.sunmail.model.Mail;
import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.util.ThemeManager;
import com.example.sunmail.viewmodel.HomeViewModel;
import com.example.sunmail.viewmodel.MailViewModel;
import com.example.sunmail.viewmodel.UserViewModel;
import com.example.sunmail.viewmodel.UserViewModelFactory;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


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
    private static boolean isThemeChanging = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme before setting content view
        int savedThemeMode = ThemeManager.getThemeMode(this);
        ThemeManager.applyTheme(savedThemeMode);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Handle system bars (status bar, navigation bar, notch)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Don't add bottom padding
            return insets;
        });

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
                    // Apply the same color generation logic as in other parts of the app
                    profileButton.setBackground(createCircleDrawable(getColorForUser(username)));
                }
                
                // Update drawer header with user information
                updateDrawerHeader(session.userName, session.email);
                
                String info = "userId=" + session.userId +
                        ", userName=" + session.userName +
                        ", email=" + session.email +
                        ", profilePicture=" + session.profilePicture +
                        ", token=" + session.token;
                Log.d("UserSessionInfo", info);

                // Welcome back message for restored session (only if not changing theme)
                if (!isThemeChanging) {
                    Toast.makeText(this, "Welcome back, " + (username != null ? username : "User") + "!", Toast.LENGTH_SHORT).show();
                }
                
                // Reset the flag after use
                isThemeChanging = false;
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
                // Toggle theme
                int currentThemeMode = ThemeManager.getThemeMode(this);
                int nextThemeMode = ThemeManager.getNextThemeMode(currentThemeMode);
                
                // Set flag to indicate theme change
                isThemeChanging = true;
                
                ThemeManager.saveThemeMode(this, nextThemeMode);
                ThemeManager.applyTheme(nextThemeMode);
                
                selectedLabel = label; // Keep current label
                message = "Theme changed to " + ThemeManager.getThemeName(nextThemeMode);
                
                // Close drawer and show message, but don't reload mails
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                return true; // Return early to avoid reloading mails
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

    /**
     * Update drawer header with dynamic user information
     */
    private void updateDrawerHeader(String userName, String userEmail) {
        // Get the header view from NavigationView
        View headerView = navigationView.getHeaderView(0);
        
        if (headerView != null) {
            // Update user avatar with first letter of username and generated color
            TextView drawerAvatar = headerView.findViewById(R.id.drawer_user_avatar);
            if (drawerAvatar != null && userName != null && !userName.isEmpty()) {
                drawerAvatar.setText(userName.substring(0, 1).toUpperCase());
                // Apply the same color generation logic as in MailAdapter and ViewMailActivity
                drawerAvatar.setBackground(createCircleDrawable(getColorForUser(userName)));
            }
            
            // Update user email
            TextView drawerEmail = headerView.findViewById(R.id.drawer_user_email);
            if (drawerEmail != null && userEmail != null && !userEmail.isEmpty()) {
                drawerEmail.setText(userEmail);
            }
        }
    }

    // Same color generation logic as in MailAdapter and ViewMailActivity
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
