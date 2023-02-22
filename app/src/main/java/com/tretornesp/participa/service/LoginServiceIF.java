package com.tretornesp.participa.service;

public interface LoginServiceIF {
    boolean login(String user, String password);
    void logout();
    boolean validate_token(String token);
    String refresh_token(String token);
}
