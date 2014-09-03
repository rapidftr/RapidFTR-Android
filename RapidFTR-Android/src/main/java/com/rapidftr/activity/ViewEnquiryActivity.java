package com.rapidftr.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.adapter.PotentialMatchesFormSectionPagerAdapter;
import com.rapidftr.forms.PotentialMatchesFormSection;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.service.EnquiryHttpDao;
import com.rapidftr.service.EnquirySyncService;
import com.rapidftr.service.LogOutService;
import com.rapidftr.task.AsyncTaskWithDialog;
import com.rapidftr.task.SyncSingleRecordTask;
import com.rapidftr.view.FormSectionView;
import com.rapidftr.view.PotentialMatchesFormSectionView;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class ViewEnquiryActivity extends BaseEnquiryActivity {

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_view_enquiry);
    }

    @Override
    protected void initializePager() {
        FormSectionView potentialMatchesView = new PotentialMatchesFormSectionView(this) {
            @Override
            protected HighlightedFieldsViewAdapter getHighlightedFieldsViewAdapter(List<BaseModel> models) {
                return new HighlightedFieldsViewAdapter(getContext(), models, Child.CHILD_FORM_NAME, ViewChildActivity.class);
            }
        };
        getPager().setAdapter(new PotentialMatchesFormSectionPagerAdapter(formSections, getModel(), getEditable(), potentialMatchesView));
        getPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getSpinner().setSelection(position);
            }
        });
    }

    @Override
    protected void initializeData(Bundle savedInstanceState) throws JSONException, IOException {
        super.initializeData(savedInstanceState);
        this.editable = false;

        this.enquiry = loadEnquiry(getIntent().getExtras(), enquiryRepository);
        PotentialMatchesFormSection section = new PotentialMatchesFormSection();
        section.setOrder(formSections.size());
        formSections.add(section);
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.edit);
        setTitle(enquiry.getShortId());
    }

    public void edit(View view) throws JSONException {
        Intent editEnquiryIntent = new Intent(this, EditEnquiryActivity.class);
        editEnquiryIntent.putExtra("id", enquiry.getUniqueId());
        startActivity(editEnquiryIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_single_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                return true;
            case R.id.sync_single:
                sync();
                return true;
            case R.id.logout:
                inject(LogOutService.class).attemptLogOut(this);
                return true;
            case R.id.info:
                startActivity(new Intent(this, InfoActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void sync() {
        SyncSingleRecordTask syncRecordTask = createSyncTaskForEnquiry();
        syncRecordTask.setActivity(this);
        RapidFtrApplication.getApplicationInstance().setAsyncTaskWithDialog((AsyncTaskWithDialog) AsyncTaskWithDialog.wrap(this, syncRecordTask, R.string.sync_progress, R.string.sync_success, R.string.sync_failure).execute(enquiry));
    }

    protected SyncSingleRecordTask createSyncTaskForEnquiry() {
        SyncSingleRecordTask syncRecordTask = new SyncSingleRecordTask(
                new EnquirySyncService(this.getContext().getSharedPreferences(), new EnquiryHttpDao(), enquiryRepository), enquiryRepository, getCurrentUser());
        return syncRecordTask;
    }
}

