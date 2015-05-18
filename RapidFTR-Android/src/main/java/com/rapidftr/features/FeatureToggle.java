package com.rapidftr.features;

import android.content.SharedPreferences;
import com.google.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;

public class FeatureToggle {

    private SharedPreferences sharedPreferences;

    @Inject
    public FeatureToggle(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean isEnabled(FEATURE feature) throws JSONException {
        JSONObject features = new JSONObject(sharedPreferences.getString("features", "{}"));
        if (features.optBoolean("Enquiries", true)) {
            return true;
        }
        return false;
    }
}
