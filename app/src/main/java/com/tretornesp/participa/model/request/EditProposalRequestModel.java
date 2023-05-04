package com.tretornesp.participa.model.request;

import com.google.gson.Gson;

import java.util.List;

public class EditProposalRequestModel {
    private String title;
    private String description;
    private List<String> photos;
    private String main_photo;

    public EditProposalRequestModel(String title, String description, List<String> photos, String main_photo) {
        this.title = title;
        this.description = description;
        this.photos = photos;
        this.main_photo = main_photo;
    }

    public EditProposalRequestModel() {
        this.title = null;
        this.description = null;
        this.photos = null;
        this.main_photo = null;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public String getMain_photo() {
        return main_photo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
