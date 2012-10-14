package com.rapidftr.utils;

import android.content.Context;
import com.rapidftr.CustomTestRunner;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.tester.org.apache.http.FakeHttpLayer;
import com.xtremelabs.robolectric.tester.org.apache.http.RequestMatcher;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.rapidftr.utils.FluentRequest.http;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class FluentRequestTest {

    private HttpResponse response;

    @Before
    public void setUp() {
        response = mock(HttpResponse.class, RETURNS_DEEP_STUBS);
    }

    @Test
    public void testSimpleGet() throws IOException {
        Robolectric.getFakeHttpLayer().addHttpResponseRule("GET", "http://example.com/", response);
        assertThat(http().host("example.com").get(), equalTo(response));
    }

    @Test
    public void testSimplePost() throws IOException {
        Robolectric.getFakeHttpLayer().addHttpResponseRule("POST", "http://example.com/", response);
        assertThat(http().host("example.com").post(), equalTo(response));
    }

    @Test
    public void testSimplePut() throws IOException {
        Robolectric.getFakeHttpLayer().addHttpResponseRule("PUT", "http://example.com/", response);
        assertThat(http().host("example.com").put(), equalTo(response));
    }

    @Test
    public void testSimpleDelete() throws IOException {
        Robolectric.getFakeHttpLayer().addHttpResponseRule("DELETE", "http://example.com/", response);
        assertThat(http().host("example.com").delete(), equalTo(response));
    }

    @Test
    public void testPort() throws IOException {
        Robolectric.getFakeHttpLayer().addHttpResponseRule("http://example.com:8080/", response);
        assertThat(http().host("example.com:8080").get(), equalTo(response));
    }

    @Test
    public void testRelativeUrl() throws IOException {
        Robolectric.getFakeHttpLayer().addHttpResponseRule("http://example.com/test", response);
        assertThat(http().path("test").host("example.com").get(), equalTo(response));
    }

    @Test
    public void testScheme() throws IOException {
        Robolectric.getFakeHttpLayer().addHttpResponseRule("https://example.com/", response);
        assertThat(http().host("https://example.com").get(), equalTo(response));
        assertThat(http().host("example.com").scheme("https").get(), equalTo(response));
    }

    @Test
    public void testParameters() throws IOException {
        Robolectric.getFakeHttpLayer().addHttpResponseRule("http://example.com/?param1=value1&param2=value2", response);
        assertThat(http().host("example.com").param("param1", "value1").param("param2", "value2").get(), equalTo(response));
    }

    @Test
    public void testDefaultAndAdditionalHeaders() throws IOException {
        RequestMatcher matcher = new FakeHttpLayer.RequestMatcherBuilder()
                                 .host("example.com")
                                 .header("Accept", "application/json")
                                 .header("header1", "value1");
        Robolectric.getFakeHttpLayer().addHttpResponseRule(matcher, response);
        assertThat(http().host("example.com").header("header1", "value1").get(), equalTo(response));
    }

    @Test
    public void testBaseUrlFromContext() throws IOException {
        Context context = mock(Context.class);
        FluentRequest http = spy(http());
        doReturn("example.com").when(http).getBaseUrl(context);
        doReturn(1234).when(http).getConnectionTimeout(context);

        Robolectric.getFakeHttpLayer().addHttpResponseRule("http://example.com/test", response);
        assertThat(http.path("/test").context(context).get(), equalTo(response));
    }

}
