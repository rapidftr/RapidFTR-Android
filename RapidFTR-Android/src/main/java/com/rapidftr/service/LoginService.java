package com.rapidftr.service;

import android.content.Context;
import org.apache.http.HttpResponse;
import com.rapidftr.http.*;

import java.io.IOException;

public class LoginService  extends Service {

    public HttpResponse login(Context context, String username, String password, String url) throws IOException {
        return httpClient.post(context, username, password, url);
    }

}
