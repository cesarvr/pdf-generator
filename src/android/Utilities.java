package com.pdf.generator;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;

/**
 * Created by cesar on 12/06/17.
 */

public class Utilities {

    private static final String TAG = "Utilities";
    private static final String FILE_PREFIX = "PDF_GENERATOR";
    private static final String FILE_EXTENSION = "pdf";

    public static ParcelFileDescriptor getCacheFile(Context ctx){
        try {
            File tmp = File.createTempFile(FILE_PREFIX, FILE_EXTENSION,  ctx.getCacheDir());
            return ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e);
        }
        return null;
    }
}
