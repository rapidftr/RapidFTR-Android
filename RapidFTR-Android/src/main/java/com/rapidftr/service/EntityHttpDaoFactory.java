package com.rapidftr.service;

import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.model.PotentialMatch;

public class EntityHttpDaoFactory {

    public static EntityHttpDao<Enquiry> createEnquiryHttpDao(
            RapidFtrApplication application, String serverUrl, String apiPath, String apiParameter) {
        return new EntityHttpDao<Enquiry>(application, serverUrl, apiPath, apiParameter) {
        };
    }

    public static EntityHttpDao<PotentialMatch> createPotentialMatchHttpDao(
            RapidFtrApplication application, String serverUrl, String apiPath, String apiParameter) {
        return new EntityHttpDao<PotentialMatch>(application, serverUrl, apiPath, apiParameter) {
        };
    }

    public static EntityHttpDao<Child> createChildHttpDao(
            RapidFtrApplication application, String serverUrl, String apiPath, String apiParameter) {
        return new EntityHttpDao<Child>(application, serverUrl, apiPath, apiParameter) {
        };
    }


}
