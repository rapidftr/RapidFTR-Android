package com.rapidftr.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.graphics.BitmapFactory.decodeByteArray;
import static android.graphics.BitmapFactory.decodeResource;

public class BitmapUtil {

    private @Getter(lazy=true) static final BitmapUtil instance = newInstance();

    public static File getStorageUri() {
        File extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File appDir = new File(extDir, "rapidftr");
        File picDir = new File(appDir, ".nomedia");

        picDir.mkdirs();
        return picDir;
    }

    public static File getTempStorageFile() {
        return new File(getStorageUri(), "temp.jpg");
    }

    private static BitmapUtil newInstance() {
        return new BitmapUtil();
    }

    private @Getter final Bitmap defaultThumbnail = loadDefaultThumbnail();

    public String bitmapToBase64(Bitmap image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 85, out);
        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
    }

    public Bitmap bitmapFromBase64(String image) {
        byte[] bitmap = Base64.decode(image, Base64.DEFAULT);
        return decodeByteArray(bitmap, 0, bitmap.length);
    }

    public Bitmap createThumbnail(Bitmap image) {
        return Bitmap.createScaledBitmap(image, 96, 96, false);
    }

    protected Bitmap loadDefaultThumbnail() {
        return decodeResource(RapidFtrApplication.getInstance().getResources(), R.drawable.no_photo_clip);
    }

}
