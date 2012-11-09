package com.rapidftr.activity;

import android.content.Intent;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.FormSection;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@RunWith(CustomTestRunner.class)
public class ViewChildActivityTest {

    protected ViewChildActivity activity;

    @Before
    public void setUp() {
        activity = spy(new ViewChildActivity());
        Robolectric.shadowOf(activity).setIntent(new Intent().putExtra("id", "id1"));
    }

    @Test
    public void testShouldSetFormSectionFromContext() {
        List<FormSection> formSections = new ArrayList<FormSection>();
        RapidFtrApplication.getInstance().setFormSections(formSections);

        activity.initializeData();
        assertThat(activity.formSections, equalTo(formSections));
    }

    @Test
    public void shouldShowErrorMessageWhenChildRecordIsNull() throws Exception {
        activity.initializeData();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.internal_error)));
    }

    @Test
    public void testEditListener() {
        doNothing().when(activity).editChild();

        activity.onCreate(null);
        activity.initializeListeners();

        activity.findViewById(R.id.submit).performClick();
        verify(activity).editChild();
    }


}
