package com.mobcom1313617012.dudetracker.restapi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FCMClient {
    private static final String FCMURL = "https://fcm.googleapis.com/";

    public static Retrofit.Builder get() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(logging);
        okHttpClient.readTimeout(30, TimeUnit.SECONDS);
        okHttpClient.connectTimeout(30, TimeUnit.SECONDS);

        return new Retrofit.Builder()
                .baseUrl(FCMURL)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create());
    }

    public static FCMInterface getInterface() {
        return get().build().create(FCMInterface.class);
    }
}
