package com.rapidftr.model;

import android.content.ContentValues;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.DatabaseSession;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.UUID;

import static com.rapidftr.database.DatabaseHelper.TABLE_CHILDREN_COLUMN_CHILD_JSON;
import static com.rapidftr.database.DatabaseHelper.TABLE_CHILDREN_COLUMN_ID;

public class Child {

    public static void save(JSONObject child, String username){
        DatabaseHelper dbHelper = new DatabaseHelper();
        DatabaseSession dbSession = dbHelper.getSession();
        ContentValues values = new ContentValues();
        values.put(TABLE_CHILDREN_COLUMN_ID, generateUniqueId(username));
        values.put(TABLE_CHILDREN_COLUMN_CHILD_JSON, child.toString());
        dbSession.insert(DatabaseHelper.TABLE_CHILDREN,values);
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
