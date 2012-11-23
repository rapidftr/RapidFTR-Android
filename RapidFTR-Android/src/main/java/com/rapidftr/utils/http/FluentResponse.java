package com.rapidftr.utils.http;

import lombok.Delegate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;

public class FluentResponse implements HttpResponse {

    private @Delegate final HttpResponse response;

    public FluentResponse(HttpResponse response) {
        this.response = response;
    }

    public boolean isSuccess() {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean equals(Object other) {
        return response.equals(other);
    }

}
