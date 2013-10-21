package com.rapidftr.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

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

        mockClientResponse(resourceUri, 404, null, null);

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

        mockClientResponse(resourceUri, 200, json, null);

        assertThat(jsonClient.get(resourceUri), is(json));
    }

    @Test
    public void get_shouldAppendQueryParamsToUri() throws Exception {
        String url = "api.root.com";
        JsonClient jsonClient = new JsonClient(client);
        final MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.putSingle("a", "one");
        queryParams.putSingle("b", "two");
        String json = "some json";

        mockClientResponse(url, 200, json, queryParams);

        jsonClient.get(url, queryParams);
    }

    @Test
    public void put_shouldDelegateToPutOnTheClient() throws Exception {
        String url = "api.root.com/api/1";
        String record = "{\"id\":\"123\", \"name\":\"record\"}";

        when(client.resource(url)).thenReturn(webResource);
        when(clientResponse.getStatus()).thenReturn(200);
        when(webResource.put(ClientResponse.class, record)).thenReturn(clientResponse);
        JsonClient client1 = new JsonClient(client);

        client1.put(url, record);
    }

    @Test
    public void put_shouldThrowApiExceptionIfNonSuccessCodeIsReturned() throws Exception {
        String url = "api.root.com/api/1";
        String record = "{\"id\":\"123\", \"name\":\"record\"}";

        when(client.resource(url)).thenReturn(webResource);
        when(clientResponse.getStatus()).thenReturn(500);
        when(webResource.put(ClientResponse.class, record)).thenReturn(clientResponse);
        JsonClient client1 = new JsonClient(client);

        try {
            client1.put(url, record);
            fail();
        } catch (ApiException e) {
            assertThat(e.getStatusCode(), is(500));
        }
    }

    @Test
    public void put_shouldThrowApiExceptionIfNotFoundCodeIsReturned() throws Exception {
        String url = "api.root.com/api/1";
        String record = "{\"id\":\"123\", \"name\":\"record\"}";

        when(client.resource(url)).thenReturn(webResource);
        when(clientResponse.getStatus()).thenReturn(404);
        when(webResource.put(ClientResponse.class, record)).thenReturn(clientResponse);
        JsonClient client1 = new JsonClient(client);

        try {
            client1.put(url, record);
            fail();
        } catch (ApiException e) {
            assertThat(e.getStatusCode(), is(404));
        }
    }

    private void mockClientResponse(String resourceUri, int statusCode, String json, MultivaluedMap<String, String> queryParams) {
        when(client.resource(resourceUri)).thenReturn(webResource);
        if(queryParams != null) {
            when(webResource.queryParams(queryParams)).thenReturn(webResource);
        }
        when(webResource.accept(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builder);
        when(builder.get(ClientResponse.class)).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(statusCode);
        when(clientResponse.getEntity(String.class)).thenReturn(json);
    }


}
