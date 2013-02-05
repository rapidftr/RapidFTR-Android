package com.rapidftr.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.rapidftr.service.LogOutService;
import com.rapidftr.task.SyncAllDataAsyncTask;
import lombok.Getter;
import lombok.Setter;

import static android.net.ConnectivityManager.EXTRA_NETWORK_INFO;

public abstract class RapidFtrActivity extends Activity {
    private
    @Getter
    @Setter
    Menu menu;

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!((NetworkInfo) intent.getParcelableExtra(EXTRA_NETWORK_INFO)).isConnected() && RapidFtrApplication.getApplicationInstance().cleanSyncTask()){
                    makeToast(R.string.network_down);
                }
            }
    };

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
        saveAlertListener(SearchActivity.class);
    }

    public void registerTabListener(View view) {
        saveAlertListener(RegisterChildActivity.class);
    }

    public void viewAllChildrenListener(View view) {
        saveAlertListener(ViewAllChildrenActivity.class);
    }

    protected void logError(String message) {
        if (message != null) {
            Log.e(RapidFtrApplication.APP_IDENTIFIER, message);
        }
    }

    protected void makeToast(int resId) {
        Toast.makeText(getContext(), getText(resId), Toast.LENGTH_LONG).show();
    }

    protected Injector getInjector() {
        return getContext().getInjector();
    }

    protected <T> T inject(Class<T> clazz) {
        return getInjector().getInstance(clazz);
    }

    public void addResultListener(int requestCode, ResultListener listener) {
        activityResultListeners.put(requestCode, listener);
    }

    protected void saveOrDiscardOrCancelChild(DialogInterface.OnClickListener listener) {
        AlertDialog.Builder saveOrDiscard = new AlertDialog.Builder(this);
        saveOrDiscard.setTitle("Choose an action").setCancelable(false);
        saveOrDiscard.setItems(new String[]{"Save", "Discard", "Cancel"}, listener);
        saveOrDiscard.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (ResultListener listener : activityResultListeners.get(requestCode)) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        setMenu(menu);
        toggleSync(menu);
        setContextToSyncTask();
        return getContext().isLoggedIn();
    }

    private void setContextToSyncTask() {
        SyncAllDataAsyncTask syncTask = (SyncAllDataAsyncTask) RapidFtrApplication.getApplicationInstance().getSyncTask();
        if (syncTask != null)
            syncTask.setContext(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.synchronize_all:
                SyncAllDataAsyncTask task = inject(SyncAllDataAsyncTask.class);
                task.setContext(this);
                task.execute();
                return true;
            case R.id.cancel_synchronize_all:
                RapidFtrApplication.getApplicationInstance().cleanSyncTask();
                return true;
            case R.id.logout:
                if (this.getClass() == RegisterChildActivity.class || this.getClass() == EditChildActivity.class) {
                    saveAlertListenerForLogout();
                } else {
                    inject(LogOutService.class).attemptLogOut(this);
                }
                return true;
        }
        return false;
    }

    private void saveAlertListenerForLogout() {
        final BaseChildActivity activity = (BaseChildActivity) this;
        DialogInterface.OnClickListener listener = createAlertDialogForLogout(activity);
        if (activity.child.isValid()) {
            saveOrDiscardOrCancelChild(listener);
        }
        else{
        inject(LogOutService.class).attemptLogOut(activity);
        }
    }

    private DialogInterface.OnClickListener createAlertDialogForLogout(final BaseChildActivity activity) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItem) {
                switch (selectedItem) {
                    case 0:
                        activity.saveChild();
                        break;
                    case 1:
                        inject(LogOutService.class).attemptLogOut(activity);
                    case 2:
                        break;
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        initializeExceptionHandler();
    }

    protected boolean shouldEnsureLoggedIn() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldEnsureLoggedIn() && !getContext().isLoggedIn()) {
            finish();
        }
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
                        Toast.makeText(getContext(), getContext().getString(R.string.internal_error), Toast.LENGTH_LONG).show();
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

    protected void toggleSync(Menu menu) {
        menu.getItem(0).setVisible(RapidFtrApplication.getApplicationInstance().getSyncTask() == null);
        menu.getItem(1).setVisible(RapidFtrApplication.getApplicationInstance().getSyncTask() != null);
    }

    private void saveAlertListener(final Class cls) {
        if ((this instanceof RegisterChildActivity && ((RegisterChildActivity) this).child.isValid()) || this instanceof EditChildActivity) {
            final BaseChildActivity activity = (BaseChildActivity) this;
            DialogInterface.OnClickListener listener = createAlertDialog(cls, activity);
            saveOrDiscardOrCancelChild(listener);
        } else {
            startActivity(new Intent(RapidFtrActivity.this, cls));
        }
    }

    private DialogInterface.OnClickListener createAlertDialog(final Class cls, final BaseChildActivity activity) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItem) {
                switch (selectedItem) {
                    case 0:
                        activity.saveChild();
                        break;
                    case 1:
                        startActivity(new Intent(RapidFtrActivity.this, cls));
                    case 2:
                        break;
                }
            }
        };
    }
}
