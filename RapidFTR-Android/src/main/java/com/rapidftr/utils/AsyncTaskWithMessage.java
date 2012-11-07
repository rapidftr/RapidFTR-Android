package com.rapidftr.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.rapidftr.R;

public class AsyncTaskWithMessage<Params, Result, Callback extends Context & AsyncTaskWithMessage.BackgroundWorker<Params, Result>>
        extends AsyncTask<Params, Void, Result> {

    public interface BackgroundWorker<Param, Result> {
        Result doInBackground(Param... params) throws Exception;
        void onSuccess() throws Exception;
    }

    protected final Callback callback;
    protected final int progressMessage;
    protected final int successMessage;
    protected final int failureMessage;
    protected ProgressDialog dialog;

    public AsyncTaskWithMessage(Callback callback, int progressMessage, int successMessage, int failureMessage) {
        this.callback = callback;
        this.progressMessage = progressMessage;
        this.successMessage = successMessage;
        this.failureMessage = failureMessage;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(callback);
        dialog.setMessage(callback.getString(progressMessage));
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Result doInBackground(Params... params) {
        try {
            return callback.doInBackground(params);
        } catch (Exception e) {
            onError();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        dialog.dismiss();
        int message = result == null ? failureMessage : successMessage;
        Toast.makeText(callback, message, Toast.LENGTH_LONG).show();

        try {
            callback.onSuccess();
        } catch (Exception e) {
            onError();
        }
    }

    protected void onError() {
        Toast.makeText(callback, R.string.internal_error, Toast.LENGTH_LONG).show();
    }

}
