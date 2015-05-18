package com.rapidftr.features;

import android.content.Context;
import android.content.SharedPreferences;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static com.rapidftr.RapidFtrApplication.SHARED_PREFERENCES_FILE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CustomTestRunner.class)
public class FeatureToggleTest {

    private RapidFtrApplication application;
    private SharedPreferences mockSharedPreferences;

    @Before
    public void setUp() throws Exception {
        application = mock(RapidFtrApplication.class);
        mockSharedPreferences = Robolectric.application.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    @Test
    public void shouldReturnTrueForBothChildrenAndEnquiriesGivenOnlyEnquiriesAreSpecified() throws JSONException {
        mockSharedPreferences.edit().putString("features", "{\"Enquiries\":\"true\", \"Children\":\"true\"}").commit();
        when(application.getSharedPreferences()).thenReturn(mockSharedPreferences);

        FeatureToggle featureToggle = new FeatureToggle(mockSharedPreferences);

        assertTrue(featureToggle.isEnabled(FEATURE.ENQUIRIES));
        assertTrue(featureToggle.isEnabled(FEATURE.CHILDREN));
    }

    @Test
    public void shouldReturnFalseGivenEnquiriesAreOff() throws JSONException {
        mockSharedPreferences.edit().putString("features", "{\"Enquiries\":\"false\", \"Children\":\"true\"}").commit();
        when(application.getSharedPreferences()).thenReturn(mockSharedPreferences);

        FeatureToggle featureToggle = new FeatureToggle(mockSharedPreferences);

        assertFalse(featureToggle.isEnabled(FEATURE.ENQUIRIES));
    }

    @Test
    public void shouldReturnTrueForEnquiriesIfFeatureTogglesDontExist() throws JSONException {
        when(application.getSharedPreferences()).thenReturn(mockSharedPreferences);

        FeatureToggle featureToggle = new FeatureToggle(mockSharedPreferences);

        assertTrue(featureToggle.isEnabled(FEATURE.ENQUIRIES));
        assertTrue(featureToggle.isEnabled(FEATURE.CHILDREN));
    }
}
