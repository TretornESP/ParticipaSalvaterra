package com.tretornesp.participa.model.request;

import com.google.gson.Gson;
import com.tretornesp.participa.model.CoordinatesModel;

import java.util.List;

public class CreateProposalRequestModel {
    private String title;
    private String description;
    private String main_photo;
    private List<String> photos;
    private CoordinatesModel coordinates;

    public CreateProposalRequestModel(String title, String description, String main_photo, List<String> photos, CoordinatesModel coordinates) {
        this.title = title;
        this.description = description;
        this.main_photo = main_photo;
        this.photos = photos;
        this.coordinates = coordinates;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMain_photo() {
        return main_photo;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public CoordinatesModel getCoordinates() {
        return coordinates;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
