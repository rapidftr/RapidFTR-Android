package com.rapidftr.utils.http;

import android.content.Context;
import com.rapidftr.CustomTestRunner;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.tester.org.apache.http.FakeHttpLayer;
import com.xtremelabs.robolectric.tester.org.apache.http.RequestMatcher;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.params.HttpConnectionParams;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;

import static com.rapidftr.utils.http.FluentRequest.http;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class FluentRequestTest {

    private FluentResponse response;

    @Before
    public void setUp() {
        Security.addProvider(new BouncyCastleProvider());
        response = mock(FluentResponse.class, RETURNS_DEEP_STUBS);
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

	@Test
	public void testTimeoutFromContext() {
		Context context = mock(Context.class);
		FluentRequest http = spy(http());

		doReturn("example.com").when(http).getBaseUrl(context);
		doReturn(1234).when(http).getConnectionTimeout(context);

		http.context(context);
		verify(http).config(HttpConnectionParams.CONNECTION_TIMEOUT, 1234);
		verify(http).config(HttpConnectionParams.SO_TIMEOUT, 1234);
	}

    @Test
    public void testGetShouldCallExecute() throws IOException {
        FluentRequest http = spy(http().host("test"));
        doReturn(null).when(http).execute(any(HttpRequestBase.class));
        http.get();
        verify(http).execute(any(HttpRequestBase.class));
    }

    @Test
    public void testPostShouldCallExecute() throws IOException {
        FluentRequest http = spy(http().host("test"));
        doReturn(null).when(http).execute(any(HttpRequestBase.class));
        http.post();
        verify(http).execute(any(HttpRequestBase.class));
    }

    @Test
    public void testPostMultiPartShouldCallExecute() throws IOException {
        FluentRequest http = spy(http().host("test"));
        doReturn(null).when(http).execute(any(HttpRequestBase.class));
        http.postWithMultipart();
        verify(http).execute(any(HttpRequestBase.class));
    }

    @Test
    public void testPutShouldCallExecute() throws IOException {
        FluentRequest http = spy(http().host("test"));
        doReturn(null).when(http).execute(any(HttpRequestBase.class));
        http.put();
        verify(http).execute(any(HttpRequestBase.class));
    }

    @Test
    public void testDeleteShouldCallExecute() throws IOException {
        FluentRequest http = spy(http().host("test"));
        doReturn(null).when(http).execute(any(HttpRequestBase.class));
        http.delete();
        verify(http).execute(any(HttpRequestBase.class));
    }

    @Test(expected = Exception.class)
    public void testExecuteShouldCallReset() throws IOException {
        FluentRequest http = spy(http());
        http.execute(mock(HttpRequestBase.class, RETURNS_DEEP_STUBS));
        verify(http).reset();
    }

    @Test
    public void shouldAddImageMultiPartsFromPhotoKeys() throws IOException, GeneralSecurityException, JSONException {
        FluentRequest fluentRequest = spy(new FluentRequest());
        String photoKeys = "[\"abcd\", \"1234\"]";
        MultipartEntity multipartEntity = spy(new MultipartEntity());
        doReturn(new ByteArrayBody("content body".getBytes(), "abcd")).when(fluentRequest).attachPhoto("abcd");
        doReturn(new ByteArrayBody("content body".getBytes(), "1234")).when(fluentRequest).attachPhoto("1234");
        fluentRequest.addPhotoToMultipart(multipartEntity, photoKeys, "child");
        verify(multipartEntity).addPart(eq("child[photo][0]"), Matchers.any(ContentBody.class));
        verify(multipartEntity).addPart(eq("child[photo][1]"), Matchers.any(ContentBody.class));
    }

    @Test @Ignore // This test alone does a *real* connection to test SSL
    public void testSSL() throws IOException {
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
        HttpResponse httpResponse = http().host("https://dev.rapidftr.com:5443/login").header("Accept", "text/html").get();
        System.out.println(httpResponse.getStatusLine());
    }

}
