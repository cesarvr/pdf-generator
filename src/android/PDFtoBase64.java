package android.print;

import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.apache.cordova.CallbackContext;
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
    private static final String FILE_PREFIX = "PDF_GENERATOR";
    private static final String FILE_EXTENSION = "pdf";
    private static final String FILE_EMPTY_ERROR = "Error: Empty PDF File";
    private static final String FILE_NOT_FOUND = "Error: Temp File Not Found";
    private static final String IO_EXCEPTION = "Error: I/O";
    private final PrintAttributes printAttributes;

    private Context ctx;
    private CallbackContext cordovaCallback;
    private File file;
    private String encodedBase64;

    public PDFtoBase64(PrintAttributes printAttributes, Context ctx, CallbackContext cordovaCallback) {
        this.printAttributes = printAttributes;
        this.ctx = ctx;
        this.cordovaCallback = cordovaCallback;
    }

    public void getAsBase64() {
        try{
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            fileInputStreamReader.close();
            encodedBase64 = Base64.encodeToString( bytes, Base64.DEFAULT );
            file.delete();

            if(encodedBase64.isEmpty()){
                cordovaCallback.error(FILE_EMPTY_ERROR);
            }else{
                cordovaCallback.success(encodedBase64);
            }
        } catch(FileNotFoundException ex) {
             Log.e(TAG, "getAsBase64 Error File Not Found: ", ex );
             cordovaCallback.error(FILE_NOT_FOUND);
        } catch(IOException ex) {
             Log.e(TAG, "getAsBase64 Error in I/O: ", ex );
             cordovaCallback.error(IO_EXCEPTION);
        }
    }

    public void process(final PrintDocumentAdapter printAdapter) {

        final CancellationSignal cancellationSignal = new CancellationSignal();


        final PageRange[] ALL_PAGES_ARRAY = new PageRange[]{PageRange.ALL_PAGES};


        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel: The action was cancelled");
            }

        });


        final PrintDocumentAdapter.WriteResultCallback myWriteResultCallback = new PrintDocumentAdapter.WriteResultCallback() {



            @Override
            public void onWriteFinished(PageRange[] pages) {
                super.onWriteFinished(pages);
                getAsBase64();
            }

            @Override
            public void onWriteCancelled() {
                super.onWriteCancelled();

                Log.d(TAG, "onWriteCancelled: Cancelled!!");
            }

            @Override
            public void onWriteFailed(CharSequence error) {
                super.onWriteFailed(error);

                Log.d(TAG, "onWriteFailed: Failed!!! " + error.toString() );
            }
        };

        final PrintDocumentAdapter.LayoutResultCallback myLayoutResultCallback = new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {

                printAdapter.onWrite(ALL_PAGES_ARRAY, getOutputFile(), cancellationSignal, myWriteResultCallback);
            }

            @Override
            public void onLayoutFailed(CharSequence error) {
                super.onLayoutFailed(error);

                Log.e(TAG, "onLayoutFailed: " + error.toString() );
            }
        };

        printAdapter.onLayout(null, printAttributes, null, myLayoutResultCallback, null);
    }


    private void Test(){


    }

    private ParcelFileDescriptor getOutputFile() {
        try {
            file = File.createTempFile(FILE_PREFIX, FILE_EXTENSION, ctx.getCacheDir());
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e);
        }
        return null;
    }
}
