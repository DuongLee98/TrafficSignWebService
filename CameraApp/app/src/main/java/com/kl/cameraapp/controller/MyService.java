package com.kl.cameraapp.controller;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface MyService {

    @POST("/api/v1/trafficsign")
    @Headers("Content-Type: application/json")
    Call<JsonObject> postImage(@Body JSONObject data);

    @GET("/")
    Call<String> getHome();
}
