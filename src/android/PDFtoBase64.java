package android.print;

import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Base64InputStream;
import android.util.Log;

import android.content.Context;
import android.util.Base64;

import org.apache.cordova.CallbackContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

// https://github.com/cesarvr/pdf-generator/issues/9
// http://www.annalytics.co.uk/android/pdf/2017/04/06/Save-PDF-From-An-Android-WebView/
public class PDFtoBase64 {

    private static final String TAG = PDFtoBase64.class.getSimpleName();
    private static final String FILE_EMPTY_ERROR = "Error: Empty PDF File";


    private final PrintAttributes printAttributes;
    private CallbackContext cordovaCallback;
    private File file;

    public PDFtoBase64( CallbackContext cordovaCallback, PrintAttributes printAttributes) {

        this.printAttributes = printAttributes;
        this.cordovaCallback = cordovaCallback;
    }

    public void getAsBase64(final ParcelFileDescriptor pdfPFD) {
        String encodedBase64 = null;

        try{
            FileInputStream fileInputStreamReader = new FileInputStream(pdfPFD.getFileDescriptor());
            // don't streamline this into one line. this does not function
            // if we try and tighten it up too much. some sort of race-condition?
            // order of operatiosn in java?
            int len = (int)pdfPFD.getStatSize();
            byte[] bytes = new byte[1024];
            fileInputStreamReader.read(bytes);
            fileInputStreamReader.close();
            encodedBase64 = Base64.encodeToString( bytes, Base64.DEFAULT );

            if(encodedBase64.isEmpty()){
                cordovaCallback.error(FILE_EMPTY_ERROR);
            }else
                cordovaCallback.success(encodedBase64);

        } catch(FileNotFoundException ex) {
             encodedBase64 = ex.getMessage();
            Log.e(TAG, "getAsBase64 Error File Not Found: ", ex );
        } catch(IOException ex) {
             encodedBase64 = ex.getMessage();
            Log.e(TAG, "getAsBase64 Error in I/O: ", ex );
        }
    }

    public void process(final PrintDocumentAdapter printAdapter, final ParcelFileDescriptor pdfPFD) {

        final PrintDocumentAdapter.WriteResultCallback myWriteResultCallback = new PrintDocumentAdapter.WriteResultCallback() {
            @Override
            public void onWriteFinished(PageRange[] pages) {

                super.onWriteFinished(pages);
                getAsBase64(pdfPFD);
            }

            @Override
            public void onWriteFailed(CharSequence error){
                Log.i(TAG, error.toString());
            }
        };

        final PrintDocumentAdapter.LayoutResultCallback myLayoutResultCallback = new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                    super.onLayoutFinished(info, changed);
            }
        };

        printAdapter.onLayout(null, printAttributes, null, myLayoutResultCallback, null);
        printAdapter.onWrite(null, pdfPFD, new CancellationSignal(), myWriteResultCallback);
    }


}
