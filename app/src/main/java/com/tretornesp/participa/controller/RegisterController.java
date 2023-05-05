package com.tretornesp.participa.controller;

import java.io.File;

import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.model.request.RegisterUserRequestModel;
import com.tretornesp.participa.service.LoginService;
import com.tretornesp.participa.service.UploadsService;
import com.tretornesp.participa.service.UserService;
import com.tretornesp.participa.util.Callback;

public class RegisterController {
    private static final String DEFAULT_PIC = "6453f8671ffff087154a44d1/7124842329069850336.jpg";
    private static final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    private UserService userService;

    private boolean isEmailValid(String email) {
        //Check if email matches the regex ^[a-zA-Z0-9_.]+[@]{1}[a-z0-9]+[\\.][a-z]+$
        return email.matches("^[a-zA-Z0-9_.]+[@]{1}[a-z0-9]+[\\.][a-z]+$");
    }

    private boolean isDniValid(String dni) {
        //Check if email matches the regex ^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$
        if (!dni.matches("^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$")) return false;
        //Check if the letter is correct
        return dni.charAt(8) == DNI_LETTERS.charAt(Integer.parseInt(dni.substring(0, 8)) % 23);
    }

    private boolean isPasswordValid(String password, String passwordConfirmation) {
        if (!password.equals(passwordConfirmation)) return false;
        //Check if password matches the regex ^(?=.*[a-z]).{6,}$
        return password.matches("^(?=.*[a-z]).{6,}$");
    }

    private void _register(String name, String email, String password, String dni, String passwordConfirmation, boolean ispublic, final Callback callback) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || dni.isEmpty() || passwordConfirmation.isEmpty()) {
            callback.onFailure("All fields are required");
            return;
        }
        if (!isEmailValid(email)) {
            callback.onFailure("Invalid email");
            return;
        }
        if (!isDniValid(dni)) {
            callback.onFailure("Invalid DNI");
            return;
        }
        if (!isPasswordValid(password, passwordConfirmation)) {
            callback.onFailure("Invalid passwords");
            return;
        }

        RegisterUserRequestModel registerUserRequestModel = new RegisterUserRequestModel(name, email, dni, DEFAULT_PIC, password, ispublic);
        UserModel user = userService.registerUser(registerUserRequestModel);
        if (user == null) {
            callback.onFailure("Error registering user");
            return;
        }
        callback.onSuccess(user);
    }

    public RegisterController() {
        userService = UserService.getInstance();
    }

    public void register(String name, String email, String password, String dni, String passwordConfirmation, boolean ispublic, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _register(name, email, password, dni, passwordConfirmation, ispublic, callback);
            }
        }).start();
    }
}
