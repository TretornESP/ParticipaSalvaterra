package com.tretornesp.participa.model.response;

import com.google.gson.Gson;
import com.tretornesp.participa.model.UserModel;

public class GetCurrentUserResponseModel {
    private UserModel user;

    private GetCurrentUserResponseModel(UserModel user) {
        this.user = user;
    }

    public UserModel getUser() {
        return user;
    }

    public static GetCurrentUserResponseModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, GetCurrentUserResponseModel.class);
    }
}
