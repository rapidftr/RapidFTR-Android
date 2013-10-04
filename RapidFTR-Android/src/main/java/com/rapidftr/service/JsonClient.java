package com.rapidftr.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class JsonClient {

    private final Client client;

    public JsonClient(Client client) {
        this.client = client;
    }

    public String get(String uri) throws ApiException {
        final ClientResponse clientResponse = client
                .resource(uri)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        return getResponseJson(clientResponse);
    }

    public void put(String uri, String content) throws ApiException {
        ClientResponse response = client.resource(uri).put(ClientResponse.class, content);
        getResponseJson(response);
    }

    public String get(String uri, MultivaluedMap<String, String> queryParams) throws ApiException {
        final ClientResponse clientResponse = client
                .resource(uri)
                .queryParams(queryParams)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);

        return getResponseJson(clientResponse);
    }

    private String getResponseJson(ClientResponse clientResponse) throws ApiException {
        if (clientResponse.getStatus() >= 400) {
            throw new ApiException(clientResponse.getStatus());
        } else {
            return clientResponse.getEntity(String.class);
        }
    }
}
