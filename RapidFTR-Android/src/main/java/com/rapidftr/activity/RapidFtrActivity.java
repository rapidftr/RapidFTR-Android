package com.rapidftr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Process;
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

    public void searchTabListener(View view) {
        startActivity(new Intent(RapidFtrActivity.this, SearchActivity.class));
    }

    public void registerTabListener(View view) {
        startActivity(new Intent(RapidFtrActivity.this, RegisterChildActivity.class));
    }

    public void viewAllChildrenListener(View view) {
        startActivity(new Intent(RapidFtrActivity.this, ViewAllChildrenActivity.class));
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

    protected <T> T inject(Class<T> clazz) {
        return getInjector().getInstance(clazz);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.synchronize_all:
            SyncAllDataAsyncTask task = inject(SyncAllDataAsyncTask.class);
            task.setContext(this);
            task.execute();
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeExceptionHandler();
    }

    protected void initializeExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread thread, final Throwable throwable) {
                Log.e(RapidFtrApplication.APP_IDENTIFIER, throwable.getMessage(), throwable);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.internal_error), Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }).start();

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                }

                Process.killProcess(Process.myPid());
                System.exit(10);
            }
        });
    }

}
