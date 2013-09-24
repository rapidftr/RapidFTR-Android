package com.rapidftr.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JsonClientTest {

    @Mock
    private Client client;

    @Mock
    private WebResource webResource;

    @Mock
    private ClientResponse clientResponse;

    @Mock
    private WebResource.Builder builder;


    @Test
    public void get_shouldThrowExceptionOn404() throws Exception {
        String apiRoot = "api.root.com";
        JsonClient jsonClient = new JsonClient(client);
        final String resourceUri = apiRoot + "/enquiries/" + 5;

        mockClientResponse(resourceUri, 404, null);

        try {
            jsonClient.get(resourceUri);
            fail();
        } catch(ApiException e) {
            assertThat(e.getStatusCode(), is(404));
        }
    }

    @Test
    public void get_shouldReturnJsonStringOn200() throws Exception {
        String apiRoot = "api.root.com";
        JsonClient jsonClient = new JsonClient(client);
        final String resourceUri = apiRoot + "/enquiries/" + 5;
        final String json = "some json";

        mockClientResponse(resourceUri, 200, json);

        assertThat(jsonClient.get(resourceUri), is(json));
    }

    private void mockClientResponse(String resourceUri, int statusCode, String json) {
        when(client.resource(resourceUri)).thenReturn(webResource);
        when(webResource.accept(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builder);
        when(builder.get(ClientResponse.class)).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(statusCode);
        when(clientResponse.getEntity(String.class)).thenReturn(json);
    }


}
