package com.tretornesp.participa.service;

import com.tretornesp.participa.repository.ServerRepository;

public class LoginService implements LoginServiceIF{

    @Override
    public boolean login(String user, String password) {
        ServerRepository repository = new ServerRepository();
        return repository.login(user, password);
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
