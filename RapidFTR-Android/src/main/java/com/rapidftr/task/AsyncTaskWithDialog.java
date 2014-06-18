package com.rapidftr.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.RapidFtrActivity;
import com.rapidftr.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

public abstract class AsyncTaskWithDialog<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    @Getter
    @Setter
    protected String successMessage = "";

    @Getter
    @Setter
    protected String failureMessage = "";
    protected Notification notification;
    protected NotificationManager notificationManager;
    protected int notificationId = 1021 + new Random().nextInt(10);


    public abstract void cancel();

    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> wrap(
            final Context context, final AsyncTaskWithDialog<Params, Progress, Result> actualTask,
            final int progressMessage, final int defaultSuccessMessage, final int defaultFailureMessage) {

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
                    dialog.dismiss();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Result result) {
                dialog.dismiss();
                String message = null;
                if (result == null || result.equals(false)) {
                    message = StringUtils.isNotEmpty(failureMessage) ? failureMessage :
                            RapidFtrApplication.getApplicationInstance().getString(defaultFailureMessage);
                } else {
                    message = StringUtils.isNotEmpty(successMessage) ? successMessage :
                            RapidFtrApplication.getApplicationInstance().getString(defaultSuccessMessage);
                }


                try {
                    actualTask.onPostExecute(result);
                } catch (Exception e) {
                    message = RapidFtrApplication.getApplicationInstance().getString(defaultFailureMessage);
                }

                RapidFtrApplication.getApplicationInstance()
                        .showNotification(notificationId,
                                context.getString(R.string.info), message);
            }

            public void cancel() {
                dialog.dismiss();
                actualTask.cancel(false);
            }
        };

    }

}
