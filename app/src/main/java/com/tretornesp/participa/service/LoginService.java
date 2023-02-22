package com.tretornesp.participa.service;

public class LoginService implements LoginServiceIF{

    @Override
    public boolean login(String user, String password) {
        return false;
    }

    @Override
    public void logout() {

    }

    @Override
    public boolean validate_token(String token) {
        return false;
    }

    @Override
    public String refresh_token(String token) {
        return null;
    }
}
