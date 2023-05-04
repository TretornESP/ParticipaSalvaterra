package com.tretornesp.participa.model.response;

import com.google.gson.Gson;

public class UploadImageResponseModel {
    private String url;

    private UploadImageResponseModel(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public static UploadImageResponseModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, UploadImageResponseModel.class);
    }
}
