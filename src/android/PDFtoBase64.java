package android.print;

import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import android.content.Context;
import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

// https://github.com/cesarvr/pdf-generator/issues/9
// http://www.annalytics.co.uk/android/pdf/2017/04/06/Save-PDF-From-An-Android-WebView/
public class PDFtoBase64 {

    private static final String TAG = PDFtoBase64.class.getSimpleName();
    private final PrintAttributes printAttributes;

    private Context ctx;
    private File file;
    public String encodedBase64;

    public PDFtoBase64(Context ctx, PrintAttributes printAttributes) {
        this.printAttributes = printAttributes;
        this.ctx = ctx;
    }

    public String getAsBase64() {
        try{
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            // don't streamline this into one line. this does not function 
            // if we try and tighten it up too much. some sort of race-condition?
            // order of operatiosn in java?
            int len = (int)file.length();
            byte[] bytes = new byte[len];
            fileInputStreamReader.read(bytes);
            fileInputStreamReader.close();
            encodedBase64 = Base64.encodeToString( bytes, Base64.DEFAULT );
            file.delete();
        } catch(FileNotFoundException ex) {
             encodedBase64 = ex.getMessage();
        } catch(IOException ex) {
             encodedBase64 = ex.getMessage();
        }
        return encodedBase64;
    }

    public void process(final PrintDocumentAdapter printAdapter) {
        final File path = ctx.getFilesDir();
        final String fileName = "temp.pdf";

        final PrintDocumentAdapter.WriteResultCallback myWriteResultCallback = new PrintDocumentAdapter.WriteResultCallback() {
            @Override
            public void onWriteFinished(PageRange[] pages) {
                super.onWriteFinished(pages);
            }
        };

        final PrintDocumentAdapter.LayoutResultCallback myLayoutResultCallback = new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                printAdapter.onWrite(null, getOutputFile(path, fileName), new CancellationSignal(), myWriteResultCallback);
            }
        };

        printAdapter.onLayout(null, printAttributes, null, myLayoutResultCallback, null);
    }

    private ParcelFileDescriptor getOutputFile(File path, String fileName) {
        if (!path.exists()) {
            path.mkdirs();
        }
        file = new File(path, fileName);
        try {
            file.createNewFile();
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e);
        }
        return null;
    }
}