package com.kl.cameraapp.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance = null;

    public static Retrofit getInstance(String baseUrl) {
        if (instance==null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return instance;
    }
}
