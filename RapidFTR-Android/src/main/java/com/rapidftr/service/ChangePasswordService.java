package com.rapidftr.service;

import com.google.inject.Inject;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;

import java.io.IOException;

public class ChangePasswordService {
    private final RapidFtrApplication context;
    private final FluentRequest fluentRequest;

    @Inject
    public ChangePasswordService(RapidFtrApplication context, FluentRequest fluentRequest) {
        this.context = context;
        this.fluentRequest = fluentRequest;
    }

    public FluentResponse updatePassword(String old_password, String new_password, String new_password_confirmation) throws IOException {
        return fluentRequest
                .path("/users/update_password")
                .context(context)
                .param("forms_change_password_form[old_password]", old_password)
                .param("forms_change_password_form[new_password]", new_password)
                .param("forms_change_password_form[new_password_confirmation]", new_password_confirmation)
                .post();
    }

}
