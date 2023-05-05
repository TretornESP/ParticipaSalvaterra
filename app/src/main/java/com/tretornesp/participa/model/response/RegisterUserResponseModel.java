package com.tretornesp.participa.model.response;

import com.google.gson.Gson;
import com.tretornesp.participa.model.UserModel;

public class RegisterUserResponseModel {
    private UserModel user;

    private RegisterUserResponseModel(UserModel user) {
        this.user = user;
    }

    public UserModel getUser() {
        return user;
    }

    public static RegisterUserResponseModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, RegisterUserResponseModel.class);
    }
}
