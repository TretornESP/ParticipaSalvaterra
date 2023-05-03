package com.tretornesp.participa.repository;

public interface RepositoryIF {
    String login(String user, String password);
    void logout();
    boolean validateToken(String token);
    String refreshToken(String token);
    String getProposals(String token, String start, int size);
}
