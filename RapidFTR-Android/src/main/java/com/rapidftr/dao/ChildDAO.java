package com.rapidftr.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.model.Child;
import lombok.Cleanup;
import org.json.JSONException;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rapidftr.database.DatabaseHelper.*;

public class ChildDAO implements Closeable {

    private final String userName;
    private final DatabaseHelper helper;
    private final DatabaseSession session;

    @Inject
    public ChildDAO(@Named("USER_NAME") String userName, DatabaseHelper helper) {
        this.userName = userName;
        this.helper = helper;
        this.session = helper.openSession();
    }

    public void clearAll() {
        session.delete("CHILDREN", " = ?", new String[]{userName});
    }


    public List<Child> getAllChildren() throws JSONException {
        @Cleanup Cursor cursor = session.rawQuery("SELECT * FROM CHILDREN WHERE OWNER_USERNAME=? ORDER BY CHILD_ID", new String[]{userName});

        List<Child> children = new ArrayList<Child>();
        while (cursor.moveToNext()) {
            children.add(new Child(cursor.getString(0)));
        }

        return children;
    }

    public void create(Child child) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(DB_CHILD_OWNER, child.getOwner());
        values.put(DB_CHILD_ID, child.getId());
        values.put(DB_CHILD_CONTENT, child.toString());

        session.insert("CHILDREN", null, values);
    }

    @Override
    public void close() throws IOException {
        session.close();
        helper.close();
    }

}
