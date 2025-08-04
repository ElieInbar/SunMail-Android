package com.example.sunmail.network;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class PersistentCookieJar implements CookieJar {

    private static final String PREFS_NAME = "cookie_prefs";
    private static final String COOKIE_KEY = "cookies";

    private SharedPreferences sharedPreferences;

    public PersistentCookieJar(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        Set<String> cookieStrings = new HashSet<>();
        for (Cookie cookie : cookies) {
            cookieStrings.add(cookie.toString());
        }
        sharedPreferences.edit().putStringSet(COOKIE_KEY, cookieStrings).apply();
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        Set<String> cookieStrings = sharedPreferences.getStringSet(COOKIE_KEY, new HashSet<>());
        List<Cookie> cookies = new ArrayList<>();
        for (String cookieString : cookieStrings) {
            Cookie cookie = Cookie.parse(url, cookieString);
            if (cookie != null) {
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    public void clearCookies() {
        sharedPreferences.edit().remove(COOKIE_KEY).apply();
    }
}
