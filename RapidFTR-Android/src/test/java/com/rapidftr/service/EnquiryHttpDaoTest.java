package com.rapidftr.service;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.model.Enquiry;
import com.xtremelabs.robolectric.Robolectric;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CustomTestRunner.class)
public class EnquiryHttpDaoTest {

    private String apiRoot = "http://root.api.com";

    @Test
    public void getEnquiryShouldRetrieveEnquiryFromApi() throws Exception {
        EnquiryHttpDao enquiryHttpDao = new EnquiryHttpDao(apiRoot);
        String url = "http://blah.com/123";

        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(200, "{\"id\":\"123\"}");

        Enquiry enquiry = enquiryHttpDao.get(url);

        assertThat(enquiry.getString("id"), is("123"));
        final RequestLine requestLine = Robolectric.getSentHttpRequest(0).getRequestLine();
        assertThat(requestLine.getUri(), is(url + "/"));
        assertThat(requestLine.getMethod(), is("GET"));
    }

    @Test
    public void updateEnquiryShouldPutEnquiryUpdateToApi() throws Exception {
        final String id = "123";
        final String json = "{\"some\":\"json\"}";
        EnquiryHttpDao enquiryHttpDao = new EnquiryHttpDao( apiRoot);

        Enquiry enquiry = mock(Enquiry.class);
        when(enquiry.get("id")).thenReturn(id);
        when(enquiry.getJsonString()).thenReturn(json);

        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(200, json);

        Enquiry updatedEnquiry = enquiryHttpDao.update(enquiry);

        final HttpRequest sentHttpRequest = Robolectric.getSentHttpRequest(0);
        final RequestLine requestLine = sentHttpRequest.getRequestLine();
        assertThat(requestLine.getUri(), is(apiRoot + "/api/enquiries/" + id + "/"));
        assertThat(requestLine.getMethod(), is("PUT"));
        assertThat((String)updatedEnquiry.get("some"), is("json"));
        // TODO test that the body is being sent
    }

    @Test
    public void getIdsOfUpdatedShouldRetrieveJsonAndReturnListOfUrls() throws Exception {
        EnquiryHttpDao enquiryHttpDao = new EnquiryHttpDao( apiRoot);

        String json = "[{\"location\":\"blah.com/1\"}, {\"location\":\"blah.com/2\"}]";
        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(200, json);

        final List<String> idsOfUpdated = enquiryHttpDao.getIdsOfUpdated(new DateTime(2013, 9, 25, 18, 7, 31, DateTimeZone.UTC));

        final String uri = Robolectric.getSentHttpRequest(0).getRequestLine().getUri();
//        assertThat(uri, is(apiRoot + "/api/enquiries/?" + "updated_after=" + URLEncoder.encode("2013-09-25 18:07:31UTC", "UTF-8")));
        assertThat(uri, containsString(apiRoot + "/api/enquiries"));

        assertThat(idsOfUpdated.get(0), is("blah.com/1"));
        assertThat(idsOfUpdated.get(1), is("blah.com/2"));
    }

    @Test
    public void createEnquiryShouldCreateRecordOnAPI() throws Exception {
        EnquiryHttpDao enquiryHttpDao = new EnquiryHttpDao(apiRoot);

        final String json = "{\"some\":\"json\"}";
        final String responseJson = "{\"some\":\"json\", \"_id\":\"123abc\"}";

        Robolectric.getFakeHttpLayer().setDefaultHttpResponse(200, responseJson);

        Enquiry enquiry = mock(Enquiry.class);
        when(enquiry.getJsonString()).thenReturn(json);

        Enquiry updatedEnquiry = enquiryHttpDao.create(enquiry);

        final HttpRequest sentHttpRequest = Robolectric.getSentHttpRequest(0);
        final RequestLine requestLine = sentHttpRequest.getRequestLine();
        assertThat(requestLine.getUri(), is(apiRoot + "/api/enquiries/"));
        assertThat(requestLine.getMethod(), is("POST"));
        assertThat((String)updatedEnquiry.get("_id"), is("123abc"));
        // TODO not sure how to test the body (currently encoded as a form param)
    }
}
