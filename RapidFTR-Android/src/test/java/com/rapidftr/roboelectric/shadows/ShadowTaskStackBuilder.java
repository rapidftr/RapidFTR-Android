package com.rapidftr.roboelectric.shadows;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowPendingIntent;

import static org.robolectric.Robolectric.shadowOf_;
import static org.robolectric.bytecode.ShadowWrangler.shadowOf;

@Implements(TaskStackBuilder.class)
public class ShadowTaskStackBuilder {

    private Context context;
    private Intent intent;

    @RealObject
    TaskStackBuilder realTaskStackBuilder;

    private void setup(Context context) {
        this.context = context;
    }

    @Implementation
    public static TaskStackBuilder create(Context context) {
        TaskStackBuilder taskStackBuilder = Robolectric.newInstance(TaskStackBuilder.class, null, null);
        ShadowTaskStackBuilder shadowTaskBuilder = (ShadowTaskStackBuilder) shadowOf_(taskStackBuilder);
        shadowTaskBuilder.setup(context);

        return taskStackBuilder;
    }

    @Implementation
    public TaskStackBuilder addNextIntent(Intent intent) {
        this.intent = intent;
        return realTaskStackBuilder;
    }

    @Implementation
    public PendingIntent getPendingIntent(int requestCode, int flag) {
        PendingIntent pendingIntent = Robolectric.newInstance(PendingIntent.class, null, null);
        ShadowPendingIntent shadowPendingIntent = (ShadowPendingIntent) Robolectric.shadowOf(pendingIntent);
        return pendingIntent;
    }

    @Implementation
    public void startActivities() {
        context.startActivity(intent);
    }
}
