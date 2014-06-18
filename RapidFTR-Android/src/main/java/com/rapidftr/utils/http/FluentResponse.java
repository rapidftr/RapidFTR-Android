package com.rapidftr.utils.http;

import android.util.Log;
import com.google.common.io.CharStreams;
import lombok.AllArgsConstructor;
import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;

@RequiredArgsConstructor(suppressConstructorProperties = true)
public class FluentResponse implements HttpResponse {

    protected
    @Delegate
    final HttpResponse response;

    public boolean isSuccess() {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    public FluentResponse ensureSuccess() throws HttpException {
        if (this.isSuccess()) {
            return this;
        } else {
            String message = "HTTP Request Failed";
            try {
                message = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            } catch (IOException e) {
                Log.e("HTTP Failure", e.getMessage(), e);
            }
            throw new HttpException(message);
        }
    }

    public boolean equals(Object other) {
        return response.equals(other);
    }

}
