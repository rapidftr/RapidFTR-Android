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
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class RegisterChildActivityTest {

    private RegisterChildActivity activity;

    @Before
    public void setUp() {
        activity = spy(new RegisterChildActivity());
    }

    @Test
    public void testInitializeEmptyChild() {
        activity.initialize();
        verify(activity).setContentView(R.layout.activity_register_child);
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

        activity.onRestoreInstanceState(bundle);
        assertThat(activity.child, equalTo(child));
    }

    @Test
    public void testInitializeChild() {
        activity.initializeData();
        assertThat(activity.child, equalTo(new Child()));
    }

    @Test
    public void testInitializeFormSections() {
        List<FormSection> formSections = (List<FormSection>) mock(List.class);
        RapidFtrApplication.getInstance().setFormSections(formSections);

        activity.initializeData();
        assertThat(activity.formSections, equalTo(formSections));
    }

    @Test
    public void testSaveListener() {
        doNothing().when(activity).saveChild();

        activity.onCreate(null);
        activity.initializeListeners();

        activity.findViewById(R.id.submit).performClick();
        verify(activity).saveChild();
    }

    @Test
    public void testToastInvalidChild() {
        Child child = mock(Child.class);
        when(child.isValid()).thenReturn(false);

        activity.child = child;
        activity.saveChild();
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.save_child_invalid)));
    }

    @Test
    public void testDoInBackground() throws JSONException {
        Child child = mock(Child.class);
        when(child.isValid()).thenReturn(true);

        doReturn(null).when(activity).doInBackground(child);
        activity.child = child;

        activity.saveChild();
        verify(activity).doInBackground(child);
    }

    @Test
    public void testSuccessfulSaveChild() throws Exception {
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        doNothing().when(activity).startActivity(captor.capture());

        activity.child = new Child("id1", "user1", null);
        activity.onSuccess();
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
    public void testSpinnerChangeWhenPagerChange() {
        Spinner spinner = mock(Spinner.class);
        doReturn(spinner).when(activity).getSpinner();

        activity.initialize();
        activity.getPager().setCurrentItem(1);
        verify(spinner).setSelection(1);
        // Unable test this now because pager.setCurrentItem doesn't trigger
        // onPageChangeListener unless its rendered on the device
    }

    @Test
    public void testPagerChangeWhenSpinnerChange() {
        ViewPager pager = mock(ViewPager.class);
        doReturn(pager).when(activity).getPager();

        activity.initialize();
        activity.getSpinner().setSelection(1);
        verify(pager).setCurrentItem(1);
        // TODO: verify(spinner).setSelection(1);
        // Can't test this now because pager.setCurrentItem doesn't trigger
        // onPageChangeListener until it is rendered on screen
    }

}
