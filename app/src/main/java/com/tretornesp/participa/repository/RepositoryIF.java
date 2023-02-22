package com.tretornesp.participa.repository;

public interface RepositoryIF {
    boolean login(String user, String password);
    void logout();
    boolean validate_token(String token);
    String refresh_token(String token);
}
