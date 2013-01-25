package com.rapidftr.utils.http;

import lombok.Delegate;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;

@RequiredArgsConstructor(suppressConstructorProperties = true)
public class FluentResponse implements HttpResponse {

    private @Delegate final HttpResponse response;

    public boolean isSuccess() {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean equals(Object other) {
        return response.equals(other);
    }

}
