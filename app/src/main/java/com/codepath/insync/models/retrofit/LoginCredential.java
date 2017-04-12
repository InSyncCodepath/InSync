package com.codepath.insync.models.retrofit;


public class LoginCredential {
    private static LoginCredential loginCredential;

    String accessKey;
    String accessToken;
    String id;

    public LoginCredential() {

    }

    public static LoginCredential getLoginCredential() {
        return loginCredential;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getId() {
        return id;
    }

    public static void setLoginCredential(LoginCredential loginCredential) {
        LoginCredential.loginCredential = loginCredential;
    }
}

