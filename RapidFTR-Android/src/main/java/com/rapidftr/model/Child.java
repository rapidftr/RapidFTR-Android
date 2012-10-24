package com.rapidftr.model;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.UUID;

public class Child {

    public static void save(JSONObject child, String username){

    }

    public static String generateUniqueId(String userName){
        StringBuffer uuid = new StringBuffer(userName);
        uuid.append(getDateString()).append(getUUID(5));
        return uuid.toString();
    }

    private static String getDateString() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH) + 1)+""+calendar.get(Calendar.DAY_OF_MONTH);
    }

    private static String getUUID(int length){
        String uuid = String.valueOf(UUID.randomUUID());
        return uuid.substring(uuid.length() - length, uuid.length()); //Fetch last 5 characrters of UUID
    }
}
