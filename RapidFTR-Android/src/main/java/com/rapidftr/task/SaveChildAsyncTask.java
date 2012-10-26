package com.rapidftr.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.inject.Inject;
import com.rapidftr.R;
import com.rapidftr.database.ChildDAO;
import com.rapidftr.model.Child;

public class SaveChildAsyncTask extends AsyncTask<Child, Void, Boolean> {

    private final ChildDAO dao;
    private final ProgressDialog dialog;
    private final Toast toast;

    @Inject
    public SaveChildAsyncTask(ChildDAO dao, Context context) {
        this.dao = dao;
        this.dialog = new ProgressDialog(context);
        this.toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage(dialog.getContext().getString(R.string.save_child_progress));
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Child... params) {
        try {
            dao.create(params[0]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.dismiss();
        toast.setText((result == null || result == false) ? R.string.save_child_failure : R.string.save_child_success);
        toast.show();
    }

}
