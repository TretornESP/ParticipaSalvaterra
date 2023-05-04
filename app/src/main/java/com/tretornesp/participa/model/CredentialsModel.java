package com.tretornesp.participa.model;

public class CredentialsModel {
    private String token;
    private String refresh;
    private UserModel user;

    public CredentialsModel(String token, String refresh, UserModel user) {
        this.token = token;
        this.refresh = refresh;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefresh() {
        return refresh;
    }

    public UserModel getUser() {
        return user;
    }
}