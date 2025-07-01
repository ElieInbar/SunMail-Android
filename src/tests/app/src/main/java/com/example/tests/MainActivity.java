package com.example.tests;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private Button openBtn;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        openBtn = findViewById(R.id.btn_open_drawer);
        navigationView = findViewById(R.id.navigation_view);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        openBtn.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navhome) {
                Toast.makeText(MainActivity.this, "Accueil cliqué !", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.navprofil) {
                Toast.makeText(MainActivity.this, "Profil cliqué !", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_settings) {
                Toast.makeText(MainActivity.this, "Paramètres cliqué !", Toast.LENGTH_SHORT).show();
            }
            // Fermer le drawer après le clic
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        });
    }
}