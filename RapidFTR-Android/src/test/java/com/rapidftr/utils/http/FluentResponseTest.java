package com.rapidftr.utils.http;

import com.rapidftr.CustomTestRunner;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

@RunWith(CustomTestRunner.class)
public class FluentResponseTest {

    @Test
    public void shouldBeValidTrueForSuccessfulResponse() {
        HttpResponse response = mock(HttpResponse.class, RETURNS_DEEP_STUBS);
        given(response.getStatusLine().getStatusCode()).willReturn(100);
        assertThat(new FluentResponse(response).isSuccess(), equalTo(false));
    }

    @Test
    public void shouldNotBeValidForFailedResponse() {
        HttpResponse response = mock(HttpResponse.class, RETURNS_DEEP_STUBS);
        given(response.getStatusLine().getStatusCode()).willReturn(200);
        assertThat(new FluentResponse(response).isSuccess(), equalTo(true));
    }


}
