package com.example.sunmail.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.sunmail.model.Label;
import com.example.sunmail.model.Mail;
import com.example.sunmail.repository.AuthRepository;
import com.example.sunmail.util.AvatarColorHelper;
import com.example.sunmail.util.ThemeManager;
import com.example.sunmail.viewmodel.HomeViewModel;
import com.example.sunmail.viewmodel.LabelViewModel;
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
    private EditText searchEditText;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private String myUserId = null;
    private String username = null;
    private String label = "inbox";
    private boolean isSearchMode = false;
    private LabelViewModel labelViewModel;
    private boolean isFirstLoad = true; // Track if this is the first load or a configuration change


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme before setting content view
        int savedThemeMode = ThemeManager.getThemeMode(this);
        ThemeManager.applyTheme(savedThemeMode);
        
        // If savedInstanceState is not null, this is a configuration change (rotation, etc.)
        if (savedInstanceState != null) {
            isFirstLoad = false;
        }
        
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
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        labelViewModel.fetchLabels();

        labelViewModel.getLabels().observe(this, labels -> {
            if (labels == null || labels.isEmpty()) return;
            addCustomLabelsToDrawer(labels);
        });

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
                // Welcome back message only for first load (real login), not for configuration changes
                if (isFirstLoad && !ThemeManager.isThemeChanging(this)) {
                    String welcomeMessage = username != null ? 
                        getString(R.string.welcome_back, username) : 
                        getString(R.string.welcome_back_user);
                    Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show();
                    isFirstLoad = false; // Ensure it only shows once
                }
                
                // Clear the theme changing flag after use
                ThemeManager.clearThemeChanging(this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mailViewModel.loadMails(label); // Reload mails when activity resumes
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current state to detect configuration changes
        outState.putBoolean("isFirstLoad", false);
    }

    private void addCustomLabelsToDrawer(List<Label> labels) {
        Menu menu = navigationView.getMenu();
        int groupId = R.id.nav_group_custom_labels;
        menu.removeGroup(groupId);

        for (Label label : labels) {
            String name = label.getName().toLowerCase();
            if (name.equals("inbox") || name.equals("spam")) continue;

            MenuItem item = menu.add(groupId, Menu.NONE, Menu.NONE, " "); // un espace


            View itemView = getLayoutInflater().inflate(R.layout.item_drawer_label, navigationView, false);
            itemView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                Log.d("LayoutDebug", "itemView width: " + itemView.getWidth());
            });


            TextView labelName = itemView.findViewById(R.id.label_name);
            ImageView optionsButton = itemView.findViewById(R.id.label_options);

            labelName.setText(label.getName());

            labelName.setOnClickListener(v -> {
                this.label = label.getName();
                mailAdapter.setCurrentLabel(label.getName());
                mailViewModel.loadMails(label.getName());
                drawerLayout.closeDrawers();
            });

            optionsButton.setOnClickListener(v -> showLabelOptionsPopup(v, label));

            item.setActionView(itemView);
        }
    }
    private void showLabelOptionsPopup(View anchor, Label label) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(getString(R.string.edit));
        popup.getMenu().add(getString(R.string.delete));

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            String editText = getString(R.string.edit);
            String deleteText = getString(R.string.delete);
            
            if (title.equals(editText)) {
                showLabelOptionsDialog(label);
            } else if (title.equals(deleteText)) {
                confirmDeleteLabel(label);
            }
            return true;
        });

        popup.show();
    }
    private void confirmDeleteLabel(Label label) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_label))
                .setMessage("Do you want to delete this label \"" + label.getName() + "\" ?")
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    labelViewModel.deleteLabel(label.getId());
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }


    private void showLabelOptionsDialog(Label label) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.edit_the_label));

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_label, null);
        EditText input = dialogView.findViewById(R.id.label_name_input);
        input.setText(label.getName());

        builder.setView(dialogView);

        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(label.getName())) {
                labelViewModel.updateLabel(label.getId(), newName);
            } else {
                Toast.makeText(this, getString(R.string.name_not_changed), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    // Method to bind layout views to variables
    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);      // Navigation drawer
        menuButton = findViewById(R.id.menu_button);          // Button to open the drawer
        navigationView = findViewById(R.id.navigation_view);  // Navigation view (menu)
        searchEditText = findViewById(R.id.search_edittext);  // Search bar
        setupSearchFunctionality(); // Setup search functionality
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
            String selectedLabel = label; // By default keep the current label

            // Handle navigation menu selections
            if (id == R.id.nav_inbox) {
                selectedLabel = "inbox";
            } else if (id == R.id.nav_starred) {
                selectedLabel = "starred";
            } else if (id == R.id.nav_important) {
                selectedLabel = "important";
            } else if (id == R.id.nav_sent) {
                selectedLabel = "sent";
            } else if (id == R.id.nav_drafts) {
                selectedLabel = "drafts";
            } else if (id == R.id.nav_all_mail) {
                selectedLabel = "all";
            } else if (id == R.id.nav_spam) {
                selectedLabel = "spam";
            } else if (id == R.id.nav_trash) {
                selectedLabel = "trash";
            } else if (id == R.id.nav_theme) {
                // Toggle theme
                int currentThemeMode = ThemeManager.getThemeMode(this);
                int nextThemeMode = ThemeManager.getNextThemeMode(currentThemeMode);
                
                // Set flag to indicate theme change BEFORE applying theme
                ThemeManager.setThemeChanging(this, true);
                
                ThemeManager.saveThemeMode(this, nextThemeMode);
                ThemeManager.applyTheme(nextThemeMode);
                
                selectedLabel = label; // Keep current label
                
                // Close drawer
                drawerLayout.closeDrawers();
                
                // The activity will be recreated, so we don't need to do anything else
                return true; // Return early to avoid reloading mails
            } else if (id == R.id.nav_help) {
                selectedLabel = "inbox";
            } else if (id == R.id.nav_create_label) {
                showCreateLabelDialog();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            if (selectedLabel != null && !selectedLabel.equals(label)) {
                label = selectedLabel;
                mailAdapter.setCurrentLabel(label);
                mailViewModel.loadMails(label);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void showCreateLabelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.create_a_new_label));

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_label, null);
        builder.setView(dialogView);

        builder.setPositiveButton(getString(R.string.create), (dialog, which) -> {
            EditText labelInput = dialogView.findViewById(R.id.label_name_input);
            String labelName = labelInput.getText().toString().trim();

            if (!labelName.isEmpty()) {
                labelViewModel.createLabel(labelName);
            } else {
                Toast.makeText(this, getString(R.string.label_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.show();
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
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_confirmation))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> homeViewModel.logout())
                .setNegativeButton(getString(R.string.no), null)
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
        return AvatarColorHelper.getColorForUser(this, userName);
    }

    private Drawable createCircleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    // Setup search functionality
    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous search if any
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    // If search is empty, return to normal mode immediately
                    if (isSearchMode) {
                        isSearchMode = false;
                        mailViewModel.loadMails(label); // Reload normal mails
                    }
                } else {
                    // Perform search with 300ms delay to avoid too many API calls
                    searchRunnable = () -> {
                        isSearchMode = true;
                        mailViewModel.searchMails(query);
                    };
                    searchHandler.postDelayed(searchRunnable, 300);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

}
