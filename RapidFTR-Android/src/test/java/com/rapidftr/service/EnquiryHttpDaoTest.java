package com.rapidftr.service;

import com.rapidftr.model.Enquiry;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnquiryHttpDaoTest {

    @Mock
    private JsonClient httpClient;
    private String apiRoot = "root.api.com";

    @Test
    public void getEnquiry_shouldRetrieveEnquiryFromApi() throws Exception {
        EnquiryHttpDao enquiryHttpDao = new EnquiryHttpDao(apiRoot);
        String url = "blah.com/123";

        when(httpClient.get(url)).thenReturn("{\"id\":\"123\"}");

        Enquiry enquiry = enquiryHttpDao.getEnquiry(url);

        assertThat(enquiry.getString("id"), is("123"));
    }

    @Test
    public void postEnquiry_shouldPostEnquiryToApi() throws Exception {
        final String id = "123";
        final String json = "some json";
        EnquiryHttpDao enquiryHttpDao = new EnquiryHttpDao( apiRoot);

        Enquiry enquiry = mock(Enquiry.class);
        when(enquiry.get("id")).thenReturn(id);
        when(enquiry.getJsonString()).thenReturn(json);

        enquiryHttpDao.updateEnquiry(enquiry);

        verify(httpClient).put(format("%s/api/enquiries/%s", apiRoot, id), json);
    }

    @Test
    public void getIdsOfUpdated_shouldRetrieveJsonAndReturnListOfUrls() throws Exception {
        EnquiryHttpDao enquiryHttpDao = new EnquiryHttpDao( apiRoot);

        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.putSingle("updated_after", "2013-09-25 18:07:31UTC");
        String json = "[{\"location\":\"blah.com/1\"}, {\"location\":\"blah.com/2\"}]";
        when(httpClient.get(format("%s/api/enquiries", apiRoot), queryParams)).thenReturn(json);

        final List<String> idsOfUpdated = enquiryHttpDao.getIdsOfUpdated(new DateTime(2013, 9, 25, 18, 7, 31, DateTimeZone.UTC));

        assertThat(idsOfUpdated.get(0), is("blah.com/1"));
        assertThat(idsOfUpdated.get(1), is("blah.com/2"));
    }


    




}
