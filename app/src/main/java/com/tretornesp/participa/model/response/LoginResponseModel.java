package com.tretornesp.participa.model.response;

import com.google.gson.Gson;
import com.tretornesp.participa.model.UserModel;

public class LoginResponseModel {
    private String message;
    private String refresh;
    private String token;
    private UserModel user;

    private LoginResponseModel(
            String message,
            String refresh,
            String token,
            UserModel user
    ) {
        this.message = message;
        this.refresh = refresh;
        this.token = token;
        this.user = user;
    }

    public static LoginResponseModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, LoginResponseModel.class);
    }

    public String getMessage() {
        return message;
    }

    public String getRefresh() {
        return refresh;
    }

    public String getToken() {
        return token;
    }

    public UserModel getUser() {
        return user;
    }
}
