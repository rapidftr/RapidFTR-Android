package com.rapidftr.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import lombok.Cleanup;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Calendar;

import static android.graphics.BitmapFactory.decodeResource;

public class PhotoCaptureHelper extends CaptureHelper {

	public static final int THUMBNAIL_WIDTH = 96;
	public static final int THUMBNAIL_HEIGHT = 96;
	public static final int JPEG_QUALITY = 85;
	public static final int PHOTO_WIDTH = 475;
	public static final int PHOTO_HEIGHT = 635;

	public PhotoCaptureHelper(RapidFtrApplication context) {
        super(context);
    }

    public File getTempCaptureFile() {
        return new File(getDir(), "temp.jpg");
    }

    public Calendar getCaptureTime() {
        Calendar calendar = Calendar.getInstance();
        long time = application.getSharedPreferences().getLong("capture_start_time", System.currentTimeMillis());
        calendar.setTimeInMillis(time);
        return calendar;
    }

    public void setCaptureTime() {
        application.getSharedPreferences().edit().putLong("capture_start_time", Calendar.getInstance().getTimeInMillis()).commit();
    }

    public Bitmap getCapture() throws IOException {
        return BitmapFactory.decodeFile(getTempCaptureFile().getAbsolutePath());
    }

    public void deleteCaptures() {
        this.deleteTempCapture();
        this.deleteGalleryCaptures();
    }

    protected void deleteTempCapture() {
        File file = getTempCaptureFile();
        if (file.exists())
            file.delete();
    }

    protected void deleteGalleryCaptures() {
        Calendar from = getCaptureTime();
        Calendar capturedDate = Calendar.getInstance();
        ContentResolver resolver = application.getContentResolver();

        @Cleanup Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{BaseColumns._ID, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA},
                null, null, null);

        while (cursor != null && cursor.moveToNext()) {
            try {
                capturedDate.setTimeInMillis(cursor.getLong(1));
                if (capturedDate.after(from)) {
                    deleteGalleryCapture(cursor.getString(0), cursor.getString(2));
                }
            } catch (Exception e) {
                Toast.makeText(RapidFtrApplication.getApplicationInstance(), R.string.photo_gallery_delete_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void deleteGalleryCapture(String id, String completeFileName) {
        File image = new File(completeFileName);
        if (image.exists())
            image.delete();

        application.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + id, null);
    }

    public Bitmap getDefaultThumbnail() {
        return decodeResource(application.getResources(), R.drawable.no_photo_clip);
    }

    public void savePhoto(Bitmap bitmap, int rotationDegree, String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
	    Bitmap rotated = rotateBitmap(bitmap, rotationDegree);
	    Bitmap scaled = scaleImageTo(rotated, PHOTO_WIDTH, PHOTO_HEIGHT);
	    save(scaled, fileNameWithoutExtension);
    }

    protected Bitmap resizeImageTo(Bitmap image, int width, int height) {
        return Bitmap.createScaledBitmap(image, width, height, false);
    }

	protected Bitmap scaleImageTo(Bitmap image, int maxWidth, int maxHeight) {
		double givenWidth = image.getWidth(), givenHeight = image.getHeight();
		double scaleRatio = 1.0;

		if (givenWidth > maxWidth || givenHeight > maxHeight) {
			if (givenWidth > givenHeight) {
				scaleRatio = maxWidth / givenWidth;
			} else {
				scaleRatio = maxHeight / givenHeight;
			}
		}

		return resizeImageTo(image, (int) (givenWidth * scaleRatio), (int) (givenHeight * scaleRatio));
	}

    protected void save(Bitmap bitmap, String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        fileNameWithoutExtension = fileNameWithoutExtension.contains(".jpg")? fileNameWithoutExtension : fileNameWithoutExtension + ".jpg";
        File file = new File(getDir(), fileNameWithoutExtension);
        if (!file.exists())
            file.createNewFile();
        @Cleanup OutputStream outputStream = getCipherOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);
    }

    public void saveThumbnail(Bitmap bitmap, int rotationDegree, String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        save(resizeImageTo(rotateBitmap(bitmap, rotationDegree), THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT), fileNameWithoutExtension + "_thumb");
    }

    public Bitmap loadThumbnail(String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        return loadPhoto(fileNameWithoutExtension + "_thumb");
    }

    public Bitmap loadPhoto(String fileNameWithoutExtension) throws IOException, GeneralSecurityException {
        @Cleanup InputStream inputStream = getDecodedImageStream(fileNameWithoutExtension);
        return BitmapFactory.decodeStream(inputStream);
    }

    public InputStream getDecodedImageStream(String fileNameWithoutExtension) throws GeneralSecurityException, IOException {
        File file = getFile(fileNameWithoutExtension, ".jpg");
        return getCipherInputStream(file);
    }

    public Bitmap getThumbnailOrDefault(String fileNameWithoutExtension) {
        try {
            getFile(fileNameWithoutExtension, ".jpg");
            return loadThumbnail(fileNameWithoutExtension);
        } catch (FileNotFoundException e) {
            return getDefaultThumbnail();
        } catch (Exception e) {
            Log.e("Child Image", "Error while getting the Thumbnail", e);
            throw new RuntimeException(e);
        }
    }

    public int getPictureRotation() throws IOException {
        ExifInterface exif = getExifInterface();
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    protected ExifInterface getExifInterface() throws IOException {
        return new ExifInterface(getTempCaptureFile().getAbsolutePath());
    }

    protected Bitmap rotateBitmap(Bitmap bitmap, int rotationDegree) throws IOException {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


}
