package com.example.sunmail.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private static final String PREF_NAME = "theme_prefs";
    private static final String KEY_THEME_MODE = "theme_mode";

    // Theme modes
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveThemeMode(Context context, int themeMode) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(KEY_THEME_MODE, themeMode);
        editor.apply();
    }

    public static int getThemeMode(Context context) {
        return getPreferences(context).getInt(KEY_THEME_MODE, THEME_LIGHT);
    }

    public static void applyTheme(int themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    public static String getThemeName(int themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                return "Light";
            case THEME_DARK:
                return "Dark";
            default:
                return "Light";
        }
    }

    private static final String KEY_THEME_CHANGING = "theme_changing";

    public static void setThemeChanging(Context context, boolean isChanging) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(KEY_THEME_CHANGING, isChanging);
        editor.apply();
    }

    public static boolean isThemeChanging(Context context) {
        return getPreferences(context).getBoolean(KEY_THEME_CHANGING, false);
    }

    public static void clearThemeChanging(Context context) {
        setThemeChanging(context, false);
    }

    public static int getNextThemeMode(int currentMode) {
        // Toggle between Light and Dark only
        return (currentMode == THEME_LIGHT) ? THEME_DARK : THEME_LIGHT;
    }
}
