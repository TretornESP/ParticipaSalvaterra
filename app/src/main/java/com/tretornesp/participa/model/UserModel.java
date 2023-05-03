package com.tretornesp.participa.model;

import com.google.gson.Gson;

import java.util.List;

public class UserModel {
    private String name;
    private String photo;
    private String uid;
    private boolean ispublic;
    private String email;
    private boolean verified;
    private List<String> liked_proposals;

    private UserModel(
            String name,
            String photo,
            String uid,
            boolean ispublic,
            String email,
            boolean verified,
            List<String> liked_proposals
    ) {
        this.name = name;
        this.photo = photo;
        this.uid = uid;
        this.ispublic = ispublic;
        this.email = email;
        this.verified = verified;
        this.liked_proposals = liked_proposals;
    }

    public static UserModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, UserModel.class);
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", uid='" + uid + '\'' +
                ", ispublic=" + ispublic +
                ", email='" + email + '\'' +
                ", verified=" + verified +
                ", liked_proposals=" + liked_proposals +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUid() {
        return uid;
    }

    public boolean isIspublic() {
        return ispublic;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerified() {
        return verified;
    }

    public List<String> getLiked_proposals() {
        return liked_proposals;
    }
}
