package com.example.sunmail.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

import android.content.Context;

import okhttp3.OkHttpClient;

// ApiClient.java
public class ApiClient {
    private static Retrofit retrofit = null;
    private static PersistentCookieJar cookieJar = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            if (cookieJar == null) {
                cookieJar = new PersistentCookieJar(context);
            }

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.120:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    public static void clearCookies() {
        if (cookieJar != null) {
            cookieJar.clearCookies();
        }
    }
}
