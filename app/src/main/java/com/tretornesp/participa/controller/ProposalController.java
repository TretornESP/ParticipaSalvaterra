package com.tretornesp.participa.controller;

import android.telecom.Call;
import android.util.Log;

import com.tretornesp.participa.model.CoordinatesModel;
import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.ProposalModel;
import com.tretornesp.participa.model.request.CreateProposalRequestModel;
import com.tretornesp.participa.model.request.EditUserRequestModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.service.ProposalService;
import com.tretornesp.participa.service.UploadsService;
import com.tretornesp.participa.service.UserService;
import com.tretornesp.participa.service.exception.TokenMissingException;
import com.tretornesp.participa.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProposalController {
    public ProposalController() {
    }

    private String uploadImage(String token, File image) {
        UploadsService uploadsService = UploadsService.getInstance();
        return uploadsService.uploadImage(token, image);
    }

    private void _create(String title, String description, File main_photo, List<File> photos, double latitude, double longitude, Callback callback) {
        Log.d("NewFragment", "_createProposal: " + title + " " + description + " " + main_photo + " " + photos + " " + latitude + " " + longitude);
        if (title == null || title.isEmpty() || title.length() < 10) {
            callback.onFailure("El título es demasiado corto");
            return;
        }
        //Check if the title contains something else than a-zA-Z0-9 and spaces
        if (!title.matches("^[a-zA-Z0-9 ]*$")) {
            callback.onFailure("El título solo puede contener letras, numeros y espacios");
            return;
        }
        if (title.length() > 40) {
            callback.onFailure("El título es demasiado largo");
            return;
        }
        if (description == null || description.isEmpty() || description.length() < 80) {
            callback.onFailure("La descripción es demasiado corta");
            return;
        }
        if (description.length() > 500) {
            callback.onFailure("La descripción es demasiado larga");
            return;
        }
        if (main_photo == null) {
            callback.onFailure("Falta la foto principal");
            return;
        }
        if (latitude < -90 || latitude > 90) {
            callback.onFailure("Latitud invalida");
            return;
        }
        if (longitude < -180 || longitude > 180) {
            callback.onFailure("Longitud invalida");
            return;
        }

        try {
            CredentialsModel credentials = LoginService.getInstance().getCredentials();
            List<String> photos_str = new ArrayList<>();
            for (File photo : photos) {
                if (photo != null) {
                    photos_str.add(uploadImage(credentials.getToken(), photo));
                }
            }

            String main_photo_str = uploadImage(credentials.getToken(), main_photo);

            if (main_photo_str == null) {
                callback.onFailure("No se ha podido subir la foto principal");
                return;
            }

            for (String photo : photos_str) {
                if (photo == null) {
                    callback.onFailure("No se ha podido subir alguna foto");
                    return;
                }
            }

            Log.d("NewFragment", "Uploaded images");
            CoordinatesModel coordinates = CoordinatesModel.fromLatLng(latitude, longitude);

            CreateProposalRequestModel request = new CreateProposalRequestModel(title, description, main_photo_str, photos_str, coordinates);
            ProposalService proposalService = ProposalService.getInstance();
            ProposalModel created = proposalService.createProposal(credentials.getToken(), request);
            Log.d("NewFragment", "Created proposal");
            if (created != null) {
                Log.d("NewFragment", "Created proposal success");
                callback.onSuccess(created);
                proposalService.invalidateCache();
            } else {
                Log.d("NewFragment", "Created proposal failure");
                callback.onFailure("La propuesta no se ha creado");
                return;
            }

        } catch (TokenMissingException e) {
            callback.onFailure("Token is missing");
        }
    }

    public void _delete(String id, Callback callback) {
        ProposalService proposalService = ProposalService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();
            proposalService.deleteProposal(credentials.getToken(), id);
            callback.onSuccess(null);
        } catch (TokenMissingException tke) {
            callback.onLoginRequired();
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void create(String title, String description, File main_photo, List<File> photos, double latitude, double longitude, Callback callback) {
        Log.d("NewFragment", "createProposal: " + title + " " + description + " " + main_photo + " " + photos + " " + latitude + " " + longitude);

        new Thread(new Runnable() {
            @Override
            public void run() {
                _create(title, description, main_photo, photos, latitude, longitude, callback);
            }
        }).start();
    }

    public void _load(String id, Callback callback) {
        ProposalService proposalService = ProposalService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();
            ProposalModel proposal = proposalService.getProposal(credentials.getToken(), id);
            if (proposal != null) {
                callback.onSuccess(proposal);
            } else {
                callback.onFailure("No se ha podido cargar la propuesta");
            }
        } catch (TokenMissingException tke) {
            callback.onLoginRequired();
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }


    public void load(String id, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _load(id, callback);
            }
        }).start();
    }

    public void delete(String id, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _delete(id, callback);
            }
        }).start();
    }
}
