package com.rapidftr.service;

public class ApiException extends Exception {
    private final int statusCode;

    public ApiException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
