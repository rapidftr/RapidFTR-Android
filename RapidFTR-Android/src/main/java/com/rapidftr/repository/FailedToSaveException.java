package com.rapidftr.repository;

import java.sql.SQLException;

public class FailedToSaveException extends SQLException {

    public FailedToSaveException(String message, long errorCode) {
        super(String.format("%s Database returned error code %s", message, errorCode));
    }
}
