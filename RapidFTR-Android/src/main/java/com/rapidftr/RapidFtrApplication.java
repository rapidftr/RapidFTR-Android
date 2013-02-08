package com.rapidftr;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.User;
import com.rapidftr.task.AsyncTaskWithDialog;
import com.rapidftr.task.SynchronisationAsyncTask;
import com.rapidftr.utils.ApplicationInjector;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class RapidFtrApplication extends Application {


    public static final String SHARED_PREFERENCES_FILE = "RAPIDFTR_PREFERENCES";
    public static final String APP_IDENTIFIER = "RapidFTR";
	public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	public static final String CURRENT_USER_PREF = "CURRENT_USER";
	public static final String SERVER_URL_PREF = "SERVER_URL";
	public static final String FORM_SECTIONS_PREF = "FORM_SECTION";

	private static @Getter RapidFtrApplication applicationInstance;

    private @Getter final Injector injector;

    protected @Getter List<FormSection> formSections;
	protected @Getter User currentUser;
    protected @Getter @Setter SynchronisationAsyncTask syncTask;
    public static final String DEFAULT_LANGUAGE = "en";
    protected @Getter @Setter AsyncTaskWithDialog asyncTaskWithDialog;

    public RapidFtrApplication() {
        this(Guice.createInjector(new ApplicationInjector()));
    }

    public RapidFtrApplication(Injector injector) {
        RapidFtrApplication.applicationInstance = this;
        this.injector = injector;
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
    }

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			reloadFormSections();
			reloadCurrentUser();
		} catch (IOException e) {
			Log.e(APP_IDENTIFIER, "Failed to load form sections", e);
		}
	}

    public void setFormSections(String formSectionResponse) throws IOException {
	    getSharedPreferences().edit().putString(FORM_SECTIONS_PREF, formSectionResponse).commit();
	    reloadFormSections();
    }

	public void setFormSections(List<FormSection> formSections) throws IOException {
		setFormSections(JSON_MAPPER.writeValueAsString(formSections.toArray()));
	}

	protected void reloadFormSections() throws IOException {
		String formSections = getSharedPreferences().getString(FORM_SECTIONS_PREF, null);
		if (formSections == null) {
			@Cleanup InputStream in = getResources().openRawResource(R.raw.form_sections);
			formSections = CharStreams.toString(new InputStreamReader(in));
		}

		this.formSections = Arrays.asList(JSON_MAPPER.readValue(formSections, FormSection[].class));
	}

	protected void setCurrentUser(String user) throws IOException {
		if (Strings.emptyToNull(user) == null) {
			getSharedPreferences().edit().remove(CURRENT_USER_PREF).commit();
		} else {
			getSharedPreferences().edit().putString(CURRENT_USER_PREF, user).commit();
		}

		reloadCurrentUser();
	}

	public void setCurrentUser(User user) throws IOException {
		setCurrentUser(user == null ? null : user.asJSON());
		if (user != null && user.getServerUrl() != null)
			getSharedPreferences().edit().putString(SERVER_URL_PREF, user.getServerUrl()).commit();
	}

	protected void reloadCurrentUser() throws IOException {
		String currentUserJson = getSharedPreferences().getString(CURRENT_USER_PREF, null);
		this.currentUser = currentUserJson == null ? null : User.readFromJSON(currentUserJson);
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
        if(syncTask != null){
            syncTask.cancel(false);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(SynchronisationAsyncTask.NOTIFICATION_ID);
            syncTask = null;
        }
        if(asyncTaskWithDialog != null) {
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
}
