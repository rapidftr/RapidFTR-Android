package com.rapidftr.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.rapidftr.task.AssignThumbnailAsyncTask;

import java.lang.ref.WeakReference;

public class ThumbnailDrawable extends ColorDrawable {
    private final WeakReference<AssignThumbnailAsyncTask> childrenListThumbnailAsyncTaskReference;

    public ThumbnailDrawable(AssignThumbnailAsyncTask task) {
        super(Color.BLACK);
        childrenListThumbnailAsyncTaskReference = new WeakReference<AssignThumbnailAsyncTask>(task);
    }

    public AssignThumbnailAsyncTask getChildrenListThumbnailAsyncTask() {
        return childrenListThumbnailAsyncTaskReference.get();
    }
}
