package com.tretornesp.participa.model.controller;

import android.widget.ImageView;

public class ImageViewLoadControllerModel {
    private ImageView image;
    private String url;

    public ImageViewLoadControllerModel(ImageView image, String url) {
        this.image = image;
        this.url = url;
    }

    public ImageView getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}
