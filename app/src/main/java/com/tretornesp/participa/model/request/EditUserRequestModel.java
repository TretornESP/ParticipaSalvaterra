package com.tretornesp.participa.model.request;

import com.google.gson.Gson;

public class EditUserRequestModel {
    private String name;
    private String photo;
    private String password;
    private Boolean ispublic;

    public EditUserRequestModel(String name, String photo, String password, boolean ispublic) {
        this.name = name;
        this.photo = photo;
        this.password = password;
        this.ispublic = ispublic;
    }

    public EditUserRequestModel() {
        this.name = null;
        this.photo = null;
        this.password = null;
        this.ispublic = null;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPublic() {
        return ispublic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPublic(boolean ispublic) {
        this.ispublic = ispublic;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
