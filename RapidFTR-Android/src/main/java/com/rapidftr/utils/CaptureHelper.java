package com.rapidftr.utils;

import android.content.Context;
import android.os.Environment;
import com.rapidftr.RapidFtrApplication;

import java.io.*;
import java.security.GeneralSecurityException;

public class CaptureHelper {
    public static final String RAPIDFTR_FOLDER = "rapidftr2";

    protected RapidFtrApplication application;

    public CaptureHelper(RapidFtrApplication context){
        application = context;
    }

    public File getDir() {
        File baseDir = getExternalStorageDir();
        if (!baseDir.canWrite())
            baseDir = getInternalStorageDir();

        File picDir = new File(baseDir, ".nomedia");
        picDir.mkdirs();
        return picDir;
    }

    protected File getExternalStorageDir() {
        File extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File picDir = new File(extDir, RAPIDFTR_FOLDER);

	    picDir.mkdirs();
	    return picDir;
    }

    protected File getInternalStorageDir() {
        return application.getDir("capture", Context.MODE_PRIVATE);
    }

    public File getFile(String fileNameWithoutExtension, String extension) throws FileNotFoundException {
        File file = new File(getDir(), fileNameWithoutExtension + extension);
        if (!file.exists())
            throw new FileNotFoundException();
        return file;
    }

    protected OutputStream getCipherOutputStream(File file, String password) throws GeneralSecurityException, IOException {
        return EncryptionUtil.getCipherOutputStream(file, password);
    }

    protected InputStream getCipherInputStream(File file, String password) throws GeneralSecurityException, IOException {
        return EncryptionUtil.getCipherInputStream(file, password);
    }
}
