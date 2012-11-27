package com.rapidftr.test.utils;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.pages.LoginPage;

import java.io.IOException;

import static com.rapidftr.utils.http.FluentRequest.http;

public class RapidFTRDatabase {
    public static void main(String args[]) throws IOException, InterruptedException {
        System.out.println("Deleting children from the url "+ LoginPage.LOGIN_URL);
        deleteChildren();
    }

    public static void deleteChildren() throws IOException, InterruptedException {
        http()
                .path("/database/delete_children")
                .context(RapidFtrApplication.getInstance())
                .delete();
        Thread.sleep(5000);
    }
}
