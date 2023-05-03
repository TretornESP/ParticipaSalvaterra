package com.tretornesp.participa.service;

public interface LoginServiceIF {
    boolean login(String user, String password);
    void logout();
    boolean validateToken(String token);
    String refreshToken(String token);
}
