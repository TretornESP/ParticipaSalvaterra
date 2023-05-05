package com.tretornesp.participa.controller;

import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.service.UserService;
import com.tretornesp.participa.service.exception.TokenMissingException;
import com.tretornesp.participa.util.Callback;

public class ProfileController {
    public ProfileController() {
    }

    private void _loadProfile(Callback callback) {
        UserService userService = UserService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();
            UserModel user = userService.getCurrentUser(credentials.getToken());
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
