package com.rapidftr.service;

import java.io.IOException;

public class ApiException extends IOException {
    private final int statusCode;

    public ApiException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
