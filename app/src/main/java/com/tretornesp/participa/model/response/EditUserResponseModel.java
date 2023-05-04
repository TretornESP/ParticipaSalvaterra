package com.tretornesp.participa.model.response;

import com.google.gson.Gson;
import com.tretornesp.participa.model.UserModel;

public class EditUserResponseModel {
    private UserModel user;

    private EditUserResponseModel(UserModel user) {
        this.user = user;
    }

    public UserModel getUser() {
        return user;
    }

    public static EditUserResponseModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, EditUserResponseModel.class);
    }
}
