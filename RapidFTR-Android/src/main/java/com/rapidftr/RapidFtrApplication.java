package com.rapidftr;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.forms.Form;
import com.rapidftr.model.User;
import com.rapidftr.task.AsyncTaskWithDialog;
import com.rapidftr.task.SynchronisationAsyncTask;
import com.rapidftr.utils.ApplicationInjector;
import com.rapidftr.utils.ResourceLoader;
import lombok.Getter;
import lombok.Setter;
import org.androidannotations.annotations.EApplication;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@EApplication
public class RapidFtrApplication extends Application {


    public static final String SHARED_PREFERENCES_FILE = "RAPIDFTR_PREFERENCES";
    public static final String APP_IDENTIFIER = "RapidFTR";
    public static final String CURRENT_USER_PREF = "CURRENT_USER";
    public static final String SERVER_URL_PREF = "SERVER_URL";
    public static final String LAST_CHILD_SYNC = "LAST_CHILD_SYNC";
    public static final String LAST_ENQUIRY_SYNC = "LAST_ENQUIRY_SYNC";
    public static final String LAST_POTENTIAL_MATCH_SYNC = "LAST_POTENTIAL_MATCH_SYNC";

    private static
    @Getter
    RapidFtrApplication applicationInstance;

    private
    @Getter
    final Injector injector;

    @Getter
    protected Map<String, Form> forms = new HashMap<String, Form>();

    protected
    @Getter
    User currentUser;
    protected
    @Getter
    @Setter
    SynchronisationAsyncTask syncTask;
    public static final String DEFAULT_LANGUAGE = "en";
    protected
    @Getter
    @Setter
    AsyncTaskWithDialog asyncTaskWithDialog;

    protected NotificationManager notificationManager;

    public RapidFtrApplication() {
        this(Guice.createInjector(new ApplicationInjector()));
    }

    public RapidFtrApplication(Injector injector) {
        RapidFtrApplication.applicationInstance = this;
        this.injector = injector;
    }

    public <T> T getBean(Class<T> type) {
        return getInjector().getInstance(type);
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            reloadCurrentUser();
            loadFeatureTogglesFrom(ResourceLoader.loadResourceFromClasspath("disabled_features.json"));
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        } catch (IOException e) {
            Log.e(APP_IDENTIFIER, "Failed to load form sections", e);
        } catch (JSONException e) {
            Log.e("DISABLED_FEATURES", "Failed to load features something went wrong");
        }
    }

    private void loadFeatureTogglesFrom(InputStream in) throws IOException, JSONException {
        String featuresJSON = CharStreams.toString(new InputStreamReader(in));
        JSONObject object = new JSONObject(featuresJSON);
        SharedPreferences.Editor editor = this.getSharedPreferences().edit();

        editor.putString("disabled_features", object.toString());

        editor.commit();
    }

    protected void setCurrentUser(String user) throws IOException {
        if (Strings.emptyToNull(user) == null) {
            getSharedPreferences().edit().remove(CURRENT_USER_PREF).commit();
        } else {
            getSharedPreferences().edit().putString(CURRENT_USER_PREF, user).commit();
        }

        reloadCurrentUser();
    }

    public void setCurrentUser(User user) {
        try {
            setCurrentUser(user == null ? null : user.asJSON());
        } catch (IOException e) {
            Log.e("Setting User", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        if (user != null && user.getServerUrl() != null)
            getSharedPreferences().edit().putString(SERVER_URL_PREF, user.getServerUrl()).commit();
    }

    protected void reloadCurrentUser() throws IOException {
        this.currentUser = getUserFromSharedPreference();
    }

    public User getUserFromSharedPreference() {
        try {
            String json = getSharedPreferences().getString(CURRENT_USER_PREF, null);
            return json == null ? null : User.readFromJSON(json);
        } catch (IOException e) {
            Log.e(APP_IDENTIFIER, "Not able to fetch user from shared preference");
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public static String getDefaultLocale() {
        User user = getApplicationInstance().getCurrentUser();
        return (user != null && user.getLanguage() != null) ? user.getLanguage() : DEFAULT_LANGUAGE;
    }

    public boolean cleanSyncTask() {
        boolean syncInProgress = (syncTask != null || asyncTaskWithDialog != null);
        if (syncTask != null) {
            syncTask.cancel(false);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(SynchronisationAsyncTask.NOTIFICATION_ID);
            syncTask = null;
        }
        if (asyncTaskWithDialog != null) {
            asyncTaskWithDialog.cancel();
            asyncTaskWithDialog = null;
        }
        return syncInProgress;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public void showNotification(int notificationId, String title, String statusText) {
        NotificationCompat.Builder builder = buildNotification(notificationId, title, statusText);

        notificationManager.notify(notificationId, builder.build());

    }

    public void showProgressNotification(int notificationId, String title,
                                         String text, int max, int progress, boolean indeterminate) {
        NotificationCompat.Builder builder = buildNotification(notificationId, title, text);
        builder.setProgress(max, progress, indeterminate);

        notificationManager.notify(notificationId, builder.build());
    }

    private NotificationCompat.Builder buildNotification(int notificationId, String title,
                                                         String text) {
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(text);

        Intent resultIntent = new Intent(this, RapidFtrActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addParentStack(this.getClass());
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        return builder;
    }

    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    public String getLanguageOfCurrentUser() {
        String languageCode = getCurrentUser().getLanguage();

        if (languageCode == null || languageCode.trim().length() == 0) {
            languageCode = "en";
        }

        return languageCode;
    }
}
