package com.tretornesp.participa.model.request;

import com.google.gson.Gson;

public class LoginRequestModel {
    private String username;
    private String password;

    private LoginRequestModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static LoginRequestModel create(String username, String password) {
        return new LoginRequestModel(username, password);
    }

    public static LoginRequestModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, LoginRequestModel.class);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
