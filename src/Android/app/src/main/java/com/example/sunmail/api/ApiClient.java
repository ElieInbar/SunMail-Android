package com.example.sunmail.api;

import android.content.Context;

import com.example.sunmail.network.PersistentCookieJar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    //windows
//    private static final String BASE_URL = "http://192.168.27.142:8080/api/";
    //mac
//    private static final String BASE_URL = "http://192.168.1.230:8080/api/";
    // For Android emulator, use 10.0.2.2 to access host machine
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    private static Retrofit retrofit;

    public static Retrofit get(Context context) {
        if (retrofit == null) {
            PersistentCookieJar cookieJar = new PersistentCookieJar(context);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY))
                    .cookieJar(cookieJar)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
