package com.tretornesp.participa.model;

public class UserModel {
    private String name;
    private String email;
    private String dni;
    private String photo;

    public UserModel(String name, String email, String dni, String photo) {
        this.name = name;
        this.email = email;
        this.dni = dni;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
