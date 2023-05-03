package com.tretornesp.participa.model;

import com.google.gson.Gson;

public class PresignResponseModel {
    private String url;

    private PresignResponseModel(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public static PresignResponseModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, PresignResponseModel.class);
    }
}
