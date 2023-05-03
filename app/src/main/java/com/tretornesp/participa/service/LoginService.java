package com.tretornesp.participa.service;

import android.util.Log;

import com.tretornesp.participa.model.LoginResponseModel;
import com.tretornesp.participa.model.UserModel;
import com.tretornesp.participa.repository.ServerRepository;

public class LoginService implements LoginServiceIF{

    private static LoginService instance = null;
    private LoginResponseModel response;

    private LoginService() {

    }

    public static LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    @Override
    public boolean login(String user, String password) {
        ServerRepository repository = new ServerRepository();
        String response = repository.login(user, password);
        if (response == null) {
            return false;
        }
        this.response = LoginResponseModel.fromJson(response);

        return true;
    }

    @Override
    public void logout() {

    }

    public String getToken() {
        return this.response.getToken();
    }

    public String getRefresh() {
        return this.response.getRefresh();
    }

    public boolean isLoggedIn() {
        return this.response != null;
    }

    public UserModel getUser() {
        return this.response.getUser();
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public String refreshToken(String token) {
        return null;
    }
}
