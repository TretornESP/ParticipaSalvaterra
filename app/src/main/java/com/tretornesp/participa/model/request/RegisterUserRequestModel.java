package com.tretornesp.participa.model.request;

import com.google.gson.Gson;

public class RegisterUserRequestModel {
    private String name;
    private String email;
    private String dni;
    private String photo;
    private String password;
    private boolean ispublic;

    public RegisterUserRequestModel(String name, String email, String dni, String photo, String password, boolean ispublic) {
        this.name = name;
        this.email = email;
        this.dni = dni;
        this.photo = photo;
        this.password = password;
        this.ispublic = ispublic;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDni() {
        return dni;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPassword() {
        return password;
    }

    public boolean isIspublic() {
        return ispublic;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
