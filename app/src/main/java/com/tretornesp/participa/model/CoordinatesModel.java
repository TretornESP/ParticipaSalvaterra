package com.tretornesp.participa.model;

import com.google.gson.Gson;

public class CoordinatesModel {
    private double lat;
    private double lng;

    private CoordinatesModel(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public static CoordinatesModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CoordinatesModel.class);
    }
}
