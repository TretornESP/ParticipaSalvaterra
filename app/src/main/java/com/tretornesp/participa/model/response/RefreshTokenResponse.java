package com.tretornesp.participa.model.response;

import com.google.gson.Gson;

public class RefreshTokenResponse {
    private String message;
    private String token;
    private String user;

    private RefreshTokenResponse(String message, String token, String user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getUser() {
        return user;
    }

    public static RefreshTokenResponse fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, RefreshTokenResponse.class);
    }

    @Override
    public String toString() {
        return "RefreshTokenResponse{" +
                "message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
