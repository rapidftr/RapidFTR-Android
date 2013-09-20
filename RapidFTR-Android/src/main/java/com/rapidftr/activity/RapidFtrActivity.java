package com.rapidftr.activity;

import android.app.AlertDialog;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.model.User;
import com.rapidftr.service.LogOutService;
import com.rapidftr.task.SynchronisationAsyncTask;
import com.rapidftr.view.fields.TextField;
import lombok.Getter;
import lombok.Setter;

import static android.net.ConnectivityManager.EXTRA_NETWORK_INFO;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;
import static com.rapidftr.RapidFtrApplication.SERVER_URL_PREF;

public abstract class RapidFtrActivity extends FragmentActivity {

	public static final String LOGOUT_INTENT_FILTER = "com.rapidftr.LOGOUT_INTENT";

    protected @Getter @Setter Menu menu;

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!((NetworkInfo) intent.getParcelableExtra(EXTRA_NETWORK_INFO)).isConnected() && getContext().cleanSyncTask()){
                    makeToast(R.string.network_down);
                }
            }
    };

    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(APP_IDENTIFIER, "Logout event received");
            finish();
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

    public void searchChildrenTabListener(View view) {
        saveAlertListener(SearchActivity.class);
    }

    public void createEnquiryTabListener(View view) {
        saveAlertListener(CreateEnquiryActivity.class);
    }


    public void registerChildTabListener(View view) {
        saveAlertListener(RegisterChildActivity.class);
    }

    public void viewAllChildrenListener(View view) {
        saveAlertListener(ViewAllChildrenActivity.class);
    }

    protected void logError(String message) {
        if (message != null) {
            Log.e(APP_IDENTIFIER, message);
        }
    }

    protected void makeToast(int resId) {
        makeToast(getText(resId).toString());
    }

    protected void makeToast(String text) {
        Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
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
        saveOrDiscard.setTitle(getString(R.string.choose_action)).setCancelable(false);
        saveOrDiscard.setItems(new String[]{getString(R.string.save), getString(R.string.discard), getString(R.string.cancel)}, listener);
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
        if (getContext().isLoggedIn()) {
            getMenuInflater().inflate(R.menu.options_menu, menu);
            setMenu(menu);
            toggleChangePassword(menu);
            toggleSync(menu);
            setContextToSyncTask();
        }
        return getContext().isLoggedIn();
    }

    private void toggleChangePassword(Menu menu) {
        menu.findItem(R.id.change_password).setVisible((this.getClass() == ChangePasswordActivity.class) ? false : getContext().getCurrentUser().isVerified());
    }

    private void setContextToSyncTask() {
        SynchronisationAsyncTask syncTask = getContext().getSyncTask();
        if (syncTask != null)
            syncTask.setContext(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_password:
                if (this.getClass() == RegisterChildActivity.class || this.getClass() == EditChildActivity.class) {
                    saveAlertListener(ChangePasswordActivity.class);
                } else {
                    startActivity(new Intent(this, ChangePasswordActivity.class));
                }
                return true;
            case R.id.synchronize_all:
                if (isNullOrEmpty(getCurrentUser().getServerUrl())) {
                    getServerAndSync();
                } else {
                    synchronise();
                }
                return true;
            case R.id.cancel_synchronize_all:
                getContext().cleanSyncTask();
                return true;
            case R.id.logout:
                if (this.getClass() == RegisterChildActivity.class || this.getClass() == EditChildActivity.class) {
                    saveAlertListenerForLogout();
                } else {
                    inject(LogOutService.class).attemptLogOut(this);
                }
                return true;
            case R.id.info:
                startActivity(new Intent(this, InfoActivity.class));
                return true;

        }
        return false;
    }

    protected void synchronise() {
        if(!this.getContext().isOnline()){
            makeToast(R.string.connection_off);
        }
        else{
            SynchronisationAsyncTask task = inject(SynchronisationAsyncTask.class);
            this.getContext().setSyncTask(task);
            task.setContext(this);
            task.execute();
        }
    }

    protected User getCurrentUser() {
        return getContext().getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        initializeExceptionHandler();
	    initializeLogoutHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }

    private void unregisterReceivers() {
        try{ unregisterReceiver(logoutReceiver); }catch (IllegalArgumentException e){ logError(e.getMessage()); }
        try{ unregisterReceiver(networkChangeReceiver); }catch (IllegalArgumentException e){ logError(e.getMessage()); }
    }

    protected void initializeLogoutHandler() {
		if (shouldEnsureLoggedIn()) {
			IntentFilter intentFilter = new IntentFilter(LOGOUT_INTENT_FILTER);
			registerReceiver(logoutReceiver, intentFilter);
		}
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

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceivers();
    }

    protected void initializeExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread thread, final Throwable throwable) {
                Log.e(APP_IDENTIFIER, throwable.getMessage(), throwable);

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
        menu.getItem(0).setVisible(getContext().getSyncTask() == null);
        menu.getItem(1).setVisible(getContext().getSyncTask() != null);
    }

    protected boolean validateTextFieldNotEmpty(int id, int messageId) {
        View view = findViewById(id);
        EditText editText;
        String value;
        if (view instanceof EditText){
            editText = (EditText) findViewById(id);
            value = getEditText(id);
        }else{
            TextField textField = (TextField) view;
            editText = (EditText) textField.findViewById(R.id.value);
            value = getText(editText);
        }

        if (value == null || "".equals(value)) {
            editText.setError(getString(messageId));
            return false;
        } else {
            return true;
        }
    }

    protected String getEditText(int resId) {
        return getText((EditText) findViewById(resId));
    }

    protected String getText(EditText editText){
        CharSequence value = editText.getText();
        return value == null ? null : value.toString().trim();
    }

    protected void saveAlertListener(final Class cls) {
        if ((this instanceof RegisterChildActivity && ((RegisterChildActivity) this).child.isValid()) || this instanceof EditChildActivity) {
            final BaseChildActivity activity = (BaseChildActivity) this;
            DialogInterface.OnClickListener listener = createAlertDialog(cls, activity);
            saveOrDiscardOrCancelChild(listener);
        } else {
            startActivity(new Intent(RapidFtrActivity.this, cls));
        }
    }

    protected DialogInterface.OnClickListener createAlertDialog(final Class cls, final BaseChildActivity activity) {
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

    protected void saveAlertListenerForLogout() {
        final BaseChildActivity activity = (BaseChildActivity) this;
        DialogInterface.OnClickListener listener = createAlertDialogForLogout(activity);
        if (activity.child.isValid()) {
            saveOrDiscardOrCancelChild(listener);
        } else {
            inject(LogOutService.class).attemptLogOut(activity);
        }
    }

    protected DialogInterface.OnClickListener createAlertDialogForLogout(final BaseChildActivity activity) {
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

    public void getServerAndSync() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter sync location");
        alert.setMessage("Please enter the the location you wish to synchronise with");
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getContext().getSharedPreferences().edit().putString(SERVER_URL_PREF, input.getText().toString()).commit();
                synchronise();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
                startActivity(new Intent(getContext(), ViewAllChildrenActivity.class));
            }
        });
        alert.create().show();
    }

    protected BroadcastReceiver getBroadcastReceiver(){
        return networkChangeReceiver;
    }
}
