package com.tretornesp.participa.model.controller;

import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;

public class ImageViewLoadMarkerControllerModel {
    private ImageView image;
    private String url;
    private Marker marker;

    public ImageViewLoadMarkerControllerModel(ImageView image, String url, Marker marker) {
        this.image = image;
        this.url = url;
        this.marker = marker;
    }

    public ImageView getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public Marker getMarker() {
        return marker;
    }
}
