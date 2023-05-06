package com.tretornesp.participa.controller;

import com.tretornesp.participa.model.CredentialsModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.model.request.EditUserRequestModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.service.UploadsService;
import com.tretornesp.participa.service.UserService;
import com.tretornesp.participa.service.exception.TokenMissingException;
import com.tretornesp.participa.util.Callback;

import java.io.File;

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

    private void _updateName(String name, Callback callback) {
        UserService userService = UserService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();

            EditUserRequestModel user = new EditUserRequestModel();
            user.setName(name);
            userService.editUser(credentials.getToken(), user);

            callback.onSuccess(name);
        } catch (TokenMissingException tke) {
            callback.onLoginRequired();
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    private void _updateImage(File file, Callback callback) {
        UserService userService = UserService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();

            UploadsService uploadsService = UploadsService.getInstance();
            String uploaded = uploadsService.uploadImage(credentials.getToken(), file);
            String signed = uploadsService.presignImage(credentials.getToken(), uploaded);

            EditUserRequestModel user = new EditUserRequestModel();
            user.setPhoto(uploaded);
            userService.editUser(credentials.getToken(), user);

            callback.onSuccess(signed);
        } catch (TokenMissingException tke) {
            callback.onLoginRequired();
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    private void _logout(Callback callback) {
        LoginService loginService = LoginService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();
            loginService.logout(credentials.getToken());
            callback.onSuccess(null);
        } catch (TokenMissingException tke) {
            callback.onSuccess(null);
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    private void _updatePassword(String newpass, Callback callback) {
        UserService userService = UserService.getInstance();
        try {
            CredentialsModel credentials = LoginService.getCredentials();

            EditUserRequestModel user = new EditUserRequestModel();
            user.setPassword(newpass);
            userService.editUser(credentials.getToken(), user);

            callback.onSuccess(newpass);
        } catch (TokenMissingException tke) {
            callback.onLoginRequired();
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
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

    public void updateImage(File image, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _updateImage(image, callback);
            }
        }).start();
    }

    public void updateName(String name, Callback callback) {
        if (name == null || name.isEmpty()) {
            callback.onFailure("Name cannot be empty");
            return;
        }
        if (name.length() > 20) {
            callback.onFailure("Name cannot be longer than 20 characters");
            return;
        }
        if (name.length() < 6) {
            callback.onFailure("Name cannot be shorter than 3 characters");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                _updateName(name, callback);
            }
        }).start();
    }

    public void updatePassword(String password, Callback callback) {
        if (password == null || password.isEmpty()) {
            callback.onFailure("Password cannot be empty");
            return;
        }
        if (password.length() > 40) {
            callback.onFailure("Password cannot be longer than 40 characters");
            return;
        }
        if (password.length() < 6) {
            callback.onFailure("Password cannot be shorter than 6 characters");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                _updatePassword(password, callback);
            }
        }).start();
    }

    public void logout(Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _logout(callback);
            }
        }).start();
    }
}
