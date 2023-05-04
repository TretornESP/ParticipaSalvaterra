package com.tretornesp.participa.service;

import com.tretornesp.participa.model.response.UploadImageResponseModel;
import com.tretornesp.participa.repository.ServerRepository;

import java.io.File;

public class UploadsService {
    private static UploadsService instance = null;

    private UploadsService() {
    }

    public static UploadsService getInstance() {
        if (instance == null) {
            instance = new UploadsService();
        }
        return instance;
    }

    public String uploadImage(String token, File image) {
        ServerRepository repository = new ServerRepository();
        try {
            String response = repository.uploadImage(token, image);
            UploadImageResponseModel model = UploadImageResponseModel.fromJson(response);
            return model.getUrl();
        } catch (Exception e) {
            return null;
        }
    }

    public String presignImage(String token, String image) {
        ServerRepository repository = new ServerRepository();
        try {
            String response = repository.presignImage(token, image);
            UploadImageResponseModel model = UploadImageResponseModel.fromJson(response);
            return model.getUrl();
        } catch (Exception e) {
            return null;
        }
    }
}
