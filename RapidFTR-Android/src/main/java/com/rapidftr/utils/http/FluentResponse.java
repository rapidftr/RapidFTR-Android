package com.rapidftr.utils.http;

import lombok.AllArgsConstructor;
import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;

import java.io.IOException;

@RequiredArgsConstructor(suppressConstructorProperties = true)
public class FluentResponse implements HttpResponse {

    protected @Delegate final HttpResponse response;

    public boolean isSuccess() {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode >= 200 && statusCode < 300;
    }

	public FluentResponse ensureSuccess() throws HttpException {
		if (this.isSuccess()) {
			return this;
		} else {
			throw new HttpException("HTTP Request Failed");
		}
	}

    public boolean equals(Object other) {
        return response.equals(other);
    }

}
