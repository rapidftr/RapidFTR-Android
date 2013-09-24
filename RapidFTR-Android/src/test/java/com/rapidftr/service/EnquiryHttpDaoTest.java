package com.rapidftr.service;

import com.rapidftr.model.Enquiry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

    @Test
    public void getEnquiry_shouldRetrieveEnquiryFromApi() throws Exception {
        String apiRoot = "root.api.com";
        EnquiryHttpDao enquiryHttpDao = new EnquiryHttpDao(httpClient, apiRoot);
        String id = "id";

        when(httpClient.get(format("%s/api/enquiries/%s", apiRoot, id))).thenReturn("{\"id\":\"123\"}");

        Enquiry enquiry = enquiryHttpDao.getEnquiry(id);

        assertThat((String)enquiry.get("id"), is("123"));
    }

    @Test
    public void postEnquiry_shouldPostEnquiryToApi() throws Exception {
        String apiRoot = "root.api.com";
        final String id = "123";
        final String json = "some json";
        EnquiryHttpDao enquiryHttpDao = new EnquiryHttpDao(httpClient, apiRoot);

        Enquiry enquiry = mock(Enquiry.class);
        when(enquiry.get("id")).thenReturn(id);
        when(enquiry.getJsonString()).thenReturn(json);

        enquiryHttpDao.postEnquiry(enquiry);

        verify(httpClient).put(format("%s/api/enquiries/%s", apiRoot, id), json);
    }

    




}
