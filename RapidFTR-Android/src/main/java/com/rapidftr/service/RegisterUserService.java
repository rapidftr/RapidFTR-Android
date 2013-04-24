package com.rapidftr.service;

import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;

import java.io.IOException;

public class RegisterUserService {
    private final RapidFtrApplication context;
    private final FluentRequest fluentRequest;

    @Inject
    public RegisterUserService(RapidFtrApplication context, FluentRequest fluentRequest) {
        this.context = context;
        this.fluentRequest = fluentRequest;
    }

    public FluentResponse register(User user) throws IOException {
        return fluentRequest
                .path("/api/register")
                .context(context)
                .param("user", user.asJSON())
                .post();
    }
}
