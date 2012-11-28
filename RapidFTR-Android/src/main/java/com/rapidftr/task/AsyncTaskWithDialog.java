package com.rapidftr.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public abstract class AsyncTaskWithDialog<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> wrap(
            final Context context, final AsyncTaskWithDialog<Params, Progress, Result> actualTask,
            final int progressMessage, final int successMessage, final int failureMessage) {

        final ProgressDialog dialog = new ProgressDialog(context);

        return new AsyncTaskWithDialog<Params, Progress, Result>() {

            @Override
            protected void onPreExecute() {
                dialog.setMessage(context.getString(progressMessage));
                dialog.setCancelable(false);
                dialog.show();

                actualTask.onPreExecute();
            }

            @Override
            protected Result doInBackground(Params... params) {
                try {
                    return actualTask.doInBackground(params);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Result result) {
                dialog.dismiss();
                int message = result == null ? failureMessage : successMessage;

                try {
                    actualTask.onPostExecute(result);
                } catch (Exception e) {
                    message = failureMessage;
                }

                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        };

    }

}
