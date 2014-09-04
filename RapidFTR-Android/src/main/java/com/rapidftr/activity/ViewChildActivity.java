package com.rapidftr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.adapter.PotentialMatchesFormSectionPagerAdapter;
import com.rapidftr.forms.PotentialMatchesFormSection;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildSyncService;
import com.rapidftr.service.LogOutService;
import com.rapidftr.task.AsyncTaskWithDialog;
import com.rapidftr.task.SyncSingleRecordTask;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.view.FormSectionView;
import com.rapidftr.view.PotentialMatchesFormSectionView;
import org.json.JSONException;

import java.util.List;

import static com.rapidftr.RapidFtrApplication.APP_IDENTIFIER;


public class ViewChildActivity extends BaseChildActivity {

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_register_child);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    edit();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void initializeLabels() throws JSONException {
        setLabel(R.string.edit);
        setTitle(child.getShortId());
    }

    @Override
    protected void saveChild() {
        //Nothing to implement
    }

    @Override
    protected void initializePager() {
        FormSectionView potentialMatchesView = new PotentialMatchesFormSectionView(this) {
            @Override
            protected HighlightedFieldsViewAdapter getHighlightedFieldsViewAdapter(List<BaseModel> models) {
                return new HighlightedFieldsViewAdapter(getContext(), models, Enquiry.ENQUIRY_FORM_NAME, ViewEnquiryActivity.class);
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
    protected void initializeData(Bundle savedInstanceState) throws JSONException {
        super.initializeData(savedInstanceState);
        this.editable = false;
        load();
        PotentialMatchesFormSection section = new PotentialMatchesFormSection();
        section.setOrder(formSections.size());
        formSections.add(section);
    }

    protected void sync() {
        SyncSingleRecordTask task = createChildSyncTask();
        task.setActivity(this);
        RapidFtrApplication.getApplicationInstance()
                .setAsyncTaskWithDialog((AsyncTaskWithDialog) AsyncTaskWithDialog.wrap(this, task, R.string.sync_progress, R.string.sync_success, R.string.sync_failure).execute(child));
    }

    protected SyncSingleRecordTask createChildSyncTask() {
        ChildRepository childRepository = inject(ChildRepository.class);
        return new SyncSingleRecordTask(new ChildSyncService(this.getContext(), childRepository, new FluentRequest()), getCurrentUser()) {
            @Override
            public Boolean doInBackground(BaseModel... params) {
                try {
                    Child childRecord = (Child) service.sync(params[0], currentUser);
                    if (!childRecord.isSynced()) {
                        RapidFtrApplication.getApplicationInstance()
                                .getAsyncTaskWithDialog().setFailureMessage(childRecord.getSyncLog());
                    }
                    return childRecord.isSynced();
                } catch (Exception e) {
                    Log.e(APP_IDENTIFIER, "Error syncing one child record", e);
                    return false;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_single_menu, menu);
        try {
            if (!child.isSynced() && child.getSyncLog() != null) {
                menu.findItem(R.id.synchronize_log).setVisible(true);
            }
            if (!getCurrentUser().isVerified()) {
                menu.findItem(R.id.sync_single).setVisible(false);
                menu.getItem(4).setVisible(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            case R.id.synchronize_log:
                showSyncLog();
                return true;
            case R.id.logout:
                inject(LogOutService.class).attemptLogOut(this);
                return true;
            case R.id.info:
                startActivity(new Intent(this, InfoActivity.class));
                return true;
        }
        return false;
    }

    protected void showSyncLog() {
        Toast.makeText(this, getText(R.string.temp_sync_error), Toast.LENGTH_LONG).show();
    }

}
