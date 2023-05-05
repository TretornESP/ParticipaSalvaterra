package com.tretornesp.participa.util;

public interface Callback {
    void onSuccess(Object data);
    void onFailure(String message);
    void onLoginRequired();
}
