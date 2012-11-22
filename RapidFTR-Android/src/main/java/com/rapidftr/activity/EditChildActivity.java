package com.rapidftr.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.rapidftr.R;
import com.rapidftr.model.Child;
import com.rapidftr.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;

public class EditChildActivity extends RegisterChildActivity {

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        load();
        super.initializeData(savedInstanceState);
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.save);
        setTitle(child.getShortId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.child_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.synchronize_child:
            sync();
            return true;
        }

        return false;
    }

    protected void sync() {
        SyncChildTask task = new SyncChildTask(inject(ChildService.class));
        task.execute(child);
    }

    public void rebuild() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @RequiredArgsConstructor(suppressConstructorProperties = true)
    protected class SyncChildTask extends AsyncTask<Child, Void, Child> {

        protected final ChildService service;

        @Override
        protected void onPreExecute() {
            makeToast(R.string.sync_progress);
        }

        @Override
        protected Child doInBackground(Child... children) {
            try {
                return service.sync(child);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Child child) {
            if (child == null) {
                makeToast(R.string.sync_failure);
            } else {
                makeToast(R.string.sync_success);
                rebuild();
            }
        }
    }

}
