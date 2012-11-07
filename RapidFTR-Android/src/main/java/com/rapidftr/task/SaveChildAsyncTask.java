package com.rapidftr.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.dao.ChildRepoistory;
import com.rapidftr.model.Child;
import lombok.Cleanup;

public class SaveChildAsyncTask extends AsyncTask<Child, Void, Boolean> {

    public interface SaveChildListener {
        void onSaveChild();
    }

    private final ChildRepoistory repoistory;
    private final ProgressDialog dialog;
    private final Toast toast;
    private final SaveChildListener saveChildListener;

    public SaveChildAsyncTask(ChildRepoistory repoistory, Context context, SaveChildListener saveChildListener) {
        this.repoistory = repoistory;
        this.dialog = new ProgressDialog(context);
        this.toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        this.saveChildListener = saveChildListener;
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
            @Cleanup ChildRepoistory repoistory = this.repoistory;
            repoistory.create(params[0]);
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

        if (result != null && result == true && saveChildListener != null) {
            saveChildListener.onSaveChild();
        }
    }

}
