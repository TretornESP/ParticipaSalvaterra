package com.tretornesp.participa.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class SSLUtil {
    private static SSLUtil instance = null;
    private static SSLContext sslContext;
    private static boolean valid = false;
    private static TrustManager TRUST_ALL_CERTS;

    private SSLUtil() {
        TRUST_ALL_CERTS = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        };

        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{TRUST_ALL_CERTS}, new java.security.SecureRandom());

            valid = true;
        } catch (Exception e) {
            valid = false;
        }
    }

    public static OkHttpClient trust_all() {
        if (instance == null) {
            instance = new SSLUtil();
        }

        if (!valid) {
            return null;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) TRUST_ALL_CERTS);
        builder.hostnameVerifier((s, sslSession) -> true);

        return builder.build();
    }
}
