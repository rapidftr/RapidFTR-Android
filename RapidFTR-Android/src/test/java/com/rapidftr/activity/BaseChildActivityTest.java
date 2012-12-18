package com.rapidftr.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Spinner;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.adapter.FormSectionPagerAdapter;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.Child;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class BaseChildActivityTest {

    private BaseChildActivity activity;

    @Before
    public void setUp() {
        activity = new BaseChildActivity() {
            @Override
            protected void initializeView() {
                setContentView(R.layout.activity_register_child);
            }

            @Override
            protected void initializeLabels() throws JSONException {
            }

            @Override
            protected void saveChild() {}
        };
        activity = spy(activity);
    }

    @Test
    public void testSaveState() throws JSONException {
        Bundle bundle = new Bundle();
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        activity.child = child;

        activity.onSaveInstanceState(bundle);
        assertThat(bundle.getString("child_state"), equalTo(child.toString()));
    }

    @Test
    public void testRestoreState() throws JSONException {
        Bundle bundle = new Bundle();
        Child child = new Child("id1", "user1", "{ 'test1' : 'value1' }");
        bundle.putString("child_state", child.toString());

        activity.onCreate(bundle);
        assertThat(activity.child, equalTo(child));
    }

    @Test
    public void testInitializeFormSections() throws JSONException {
        List<FormSection> formSections = (List<FormSection>) mock(List.class);
        RapidFtrApplication.getApplicationInstance().setFormSections(formSections);

        activity.initializeData(null);
        assertThat(activity.formSections, equalTo(formSections));
    }

    @Test
    public void testShouldNotInitializeChildIfAlreadyRestored() throws JSONException {
        Child child = mock(Child.class);
        activity.child = child;

        activity.onCreate(null);
        assertThat(activity.child, equalTo(child));
    }

    @Test
    public void testViewChild() throws Exception {
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        doNothing().when(activity).startActivity(captor.capture());

        activity.child = new Child("id1", "user1", null);
        activity.view();
        Intent intent = captor.getValue();

        assertThat(intent.getComponent(), equalTo(new ComponentName(activity.getContext(), ViewChildActivity.class)));
        assertThat(intent.getStringExtra("id"), equalTo("id1"));
    }

    @Test
    public void testPagerAdapter() throws Exception {
        Child child = activity.child = mock(Child.class);
        List<FormSection> formSections = activity.formSections = (List<FormSection>) mock(List.class);
        boolean editable = activity.editable = false;

        ViewPager pager = mock(ViewPager.class);
        doReturn(pager).when(activity).getPager();

        activity.initializePager();
        verify(pager).setAdapter(eq(new FormSectionPagerAdapter(formSections, child, editable)));
    }

    @Test @Ignore
    public void testSpinnerChangeWhenPagerChange() throws JSONException {
        Spinner spinner = mock(Spinner.class);
        doReturn(spinner).when(activity).getSpinner();

        activity.onCreate(null);
        activity.getPager().setCurrentItem(1);
        verify(spinner).setSelection(1);
        // Unable test this now because pager.setCurrentItem doesn't trigger
        // onPageChangeListener unless its rendered on the device
    }

    @Test
    public void testPagerChangeWhenSpinnerChange() throws JSONException {
        ViewPager pager = mock(ViewPager.class);
        doReturn(pager).when(activity).getPager();

        activity.onCreate(null);
        activity.getSpinner().setSelection(1);
        verify(pager).setCurrentItem(1);
    }

    @Test
    public void testToastInvalidChild() throws JSONException {
        Child child = mock(Child.class);
        when(child.isValid()).thenReturn(false);

        activity.child = child;
        activity.save();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.save_child_invalid)));
    }

    @Test
    public void shouldMarkChildSyncStateToFalseWhenEverChildIsSaved() throws Exception {
        activity.child = new Child("id1", "user1", "{ 'test1' : 'value1', 'test2' : 0, 'test3' : [ '1', 2, '3' ] }");
        activity.child.setSynced(true);
        activity.save();
        assertEquals(false, activity.child.isSynced());
    }

    @Test
    public void SaveChildTaskOnPreExecuteShouldNotCallViewIfResultIsNull() throws JSONException {
        BaseChildActivity.SaveChildTask saveChildTask = activity.getSaveChildTask();
        saveChildTask.onPostExecute(null);
        verify(activity, never()).view();
    }

}
