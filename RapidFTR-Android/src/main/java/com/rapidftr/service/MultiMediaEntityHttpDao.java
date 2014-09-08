package com.rapidftr.service;

import com.rapidftr.model.BaseModel;

public class MultiMediaEntityHttpDao<T extends BaseModel> extends EntityHttpDao<T> {
    public MultiMediaEntityHttpDao() {
        super();
    }

    public MultiMediaEntityHttpDao(String serverUrl, String apiPath, String apiParamter) {
        super(serverUrl, apiPath, apiParamter);
    }


}
