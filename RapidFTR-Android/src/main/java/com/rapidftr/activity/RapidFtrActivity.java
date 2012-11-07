package com.rapidftr.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.service.FormService;
import com.rapidftr.task.SyncAllDataAsyncTask;

public abstract class RapidFtrActivity extends Activity {

    public interface ResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    protected Multimap<Integer, ResultListener> activityResultListeners = HashMultimap.create();

    public RapidFtrApplication getContext() {
        return (RapidFtrApplication) getApplication();
    }

    protected void startActivityOn(int viewResId, final Class<? extends RapidFtrActivity> activityClass) {
        findViewById(viewResId).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(RapidFtrActivity.this, activityClass));
            }
        });
    }

    protected void logError(String message) {
        if(message!=null){
           Log.e(RapidFtrApplication.APP_IDENTIFIER, message);
        }

    }

    protected void logDebug(String message) {
        Log.d(RapidFtrApplication.APP_IDENTIFIER, message);
    }

    protected void makeToast(int resId) {
        Toast.makeText(this, getText(resId), Toast.LENGTH_LONG).show();
    }

    protected Injector getInjector() {
        return ((RapidFtrApplication) getApplication()).getInjector();
    }

    public void addResultListener(int requestCode, ResultListener listener) {
        activityResultListeners.put(requestCode, listener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (ResultListener listener : activityResultListeners.get(requestCode)) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return RapidFtrApplication.getInstance().isLoggedIn();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        new SyncAllDataAsyncTask(new FormService(getContext())).execute();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
