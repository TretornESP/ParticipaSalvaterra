package com.tretornesp.participa.repository;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Requester {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static Requester instance;
    private final OkHttpClient client;

    private Requester() {
        client = new OkHttpClient();
    }

    //This breaks abstraction, but i don't really mind
    public void postJson(String url, String bodyString, Callback callback) throws IOException {
        RequestBody body = RequestBody.create(bodyString, JSON);

        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public static Requester getRequester() {
        if (instance == null)
            instance = new Requester();
        return instance;
    }
}
