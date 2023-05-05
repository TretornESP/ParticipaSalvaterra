package com.tretornesp.participa.controller;

import com.tretornesp.participa.model.CoordinatesModel;
import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.model.request.CreateProposalRequestModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.service.ProposalService;
import com.tretornesp.participa.service.UploadsService;
import com.tretornesp.participa.service.exception.TokenMissingException;
import com.tretornesp.participa.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProposalController {

    private void _create(String title, String description, File main_photo, List<File> photos, double latitude, double longitude, Callback callback) {
        if (title == null || title.isEmpty()) {
            callback.onFailure("Title is empty");
        }
        if (description == null || description.isEmpty()) {
            callback.onFailure("Description is empty");
        }
        if (main_photo == null) {
            callback.onFailure("Main photo is empty");
        }
        if (latitude == 0 || longitude == 0) {
            callback.onFailure("Coordinates are empty");
        }

        try {
            CredentialsModel credentials = LoginService.getInstance().getCredentials();
            UploadsService uploadsService = UploadsService.getInstance();
            List<String> photos_str = new ArrayList<>();
            for (File photo : photos) {
                if (photo != null) {
                    photos_str.add(uploadsService.uploadImage(credentials.getToken(), photo));
                }
            }
            String main_photo_str = uploadsService.uploadImage(credentials.getToken(), main_photo);
            CoordinatesModel coordinates = CoordinatesModel.fromLatLng(latitude, longitude);

            CreateProposalRequestModel request = new CreateProposalRequestModel(title, description, main_photo_str, photos_str, coordinates);
            ProposalService proposalService = ProposalService.getInstance();
            ProposalModel created = proposalService.createProposal(credentials.getToken(), request);
            if (created != null) {
                callback.onSuccess(created);
            } else {
                callback.onFailure("Error creating proposal");
            }

        } catch (TokenMissingException e) {
            callback.onFailure("Token is missing");

        }

    }
}
