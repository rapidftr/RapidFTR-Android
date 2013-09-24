package com.rapidftr.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.MediaType;

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

        if(clientResponse.getStatus() >= 400){
            throw new ApiException(clientResponse.getStatus());
        } else {
            return clientResponse.getEntity(String.class);
        }
    }

    public void put(String uri, String content) throws ApiException {
        throw new UnsupportedOperationException("implement me");
    }

}
