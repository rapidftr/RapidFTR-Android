package com.rapidftr.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import lombok.Cleanup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.graphics.BitmapFactory.decodeResource;

public class CaptureHelper {

    public File getCaptureDir() {
        File extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File appDir = new File(extDir, "rapidftr");
        File picDir = new File(appDir, ".nomedia");

        picDir.mkdirs();
        return picDir;
    }

    public File getTempCaptureFile() {
        return new File(getCaptureDir(), "temp.jpg");
    }

    public void deleteTempCaptureFile() {
        File file = getTempCaptureFile();
        if (file.exists())
            file.delete();
    }

    public Bitmap getCaptureBitmap() {
        return BitmapFactory.decodeFile(getTempCaptureFile().getAbsolutePath());
    }

    public void deleteCapturesAfter(Calendar since) {
        Calendar date = Calendar.getInstance();
        ContentResolver resolver = RapidFtrApplication.getInstance().getContentResolver();

        @Cleanup Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.ImageColumns.DATE_TAKEN,  MediaStore.Images.ImageColumns.DATA },
                null, null, null);

        while (cursor.moveToNext()) {
            date.setTimeInMillis(cursor.getLong(0));
            if (date.after(since))
                deleteCapture(cursor.getString(1));
        }
    }

    protected void deleteCapture(String data) {
        File image = new File(data);
        if (image.exists())
            image.delete();
    }

    public Bitmap getDefaultThumbnail() {
        return decodeResource(RapidFtrApplication.getInstance().getResources(), R.drawable.no_photo_clip);
    }

    public Bitmap createThumbnail(Bitmap image) {
        return Bitmap.createScaledBitmap(image, 96, 96, false);
    }

    public Bitmap save(Bitmap bitmap, String fileNameWithoutExtension) throws IOException {
        File file = new File(getCaptureDir(), fileNameWithoutExtension + ".jpg");
        if (!file.exists())
            file.createNewFile();

        @Cleanup FileOutputStream outStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outStream);
        return bitmap;
    }

    public Bitmap saveThumbnail(Bitmap bitmap, String fileNameWithoutExtension) throws IOException {
        return save(createThumbnail(bitmap), fileNameWithoutExtension + "_thumb");
    }

    public Bitmap loadThumbnail(String fileNameWithoutExtension) throws IOException {
        return load(fileNameWithoutExtension + "_thumb");
    }

    public Bitmap load(String fileNameWithoutExtension) throws IOException {
        File inFile = new File(getCaptureDir(), fileNameWithoutExtension + ".jpg");
        if (!inFile.exists())
            throw new FileNotFoundException();

        return BitmapFactory.decodeFile(inFile.getAbsolutePath());
    }

    public Bitmap loadThumbnailOrDefault(String fileNameWithoutExtension) {
        Bitmap bitmap = null;
        try {
            if (fileNameWithoutExtension != null) {
                bitmap = loadThumbnail(fileNameWithoutExtension);
            }
        } catch (Exception e) { }

        return bitmap != null ? bitmap : getDefaultThumbnail();
    }

}
