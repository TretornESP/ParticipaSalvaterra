package com.tretornesp.participa.controller;

import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.service.UploadsService;
import com.tretornesp.participa.service.UserService;
import com.tretornesp.participa.service.exception.TokenMissingException;
import com.tretornesp.participa.util.Callback;

public class ProfileController {
    public ProfileController() {
    }

    private void _loadProfile(Callback callback) {
        UserService userService = UserService.getInstance();
        UploadsService uploadsService = UploadsService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();
            UserModel user = userService.getCurrentUser(credentials.getToken());
            if (!user.getPhoto().contains("http")) {
                String photo = uploadsService.presignImage(credentials.getToken(), user.getPhoto());
                user.setPhoto(photo);
            }
            callback.onSuccess(user);
        } catch (TokenMissingException tke) {
            callback.onLoginRequired();
        }
    }

    public void loadProfile(Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _loadProfile(callback);
            }
        }).start();
    }
}
