package com.example.sunmail.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ApiClient {

    private static final String BASE_URL = "http://172.20.10.8:8080/api/"; // TODO-עדכני
    private static Retrofit retrofit;

    public static Retrofit get() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(new OkHttpClient
                            .Builder()
                            .addInterceptor(new HttpLoggingInterceptor()
                                    .setLevel(HttpLoggingInterceptor.Level.BODY))
                            .build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
