package android.print;

import android.util.Log;
import org.apache.cordova.LOG;

public class PDFConfig {
    private static final String APPNAME ="PDFConfig";
    private PrintDocumentAdapter.LayoutResultCallback layout = null;

    public PDFConfig() {
        layout = new PrintDocumentAdapter.LayoutResultCallback() {
            public void onLayoutCancelled() {
                Log.i(APPNAME, "onLayoutCancelled called: so this was cancelled ?");
            }

            public void onLayoutFailed(CharSequence error) {
                Log.i(APPNAME, "onLayoutCancelledonLayoutFailed called ->" + error.toString());
            }

            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                Log.i(APPNAME, "onLayoutFinished called ->" + info.toString());
            }
        };
    }

    public PrintDocumentAdapter.LayoutResultCallback getLayout() {
        return layout;
    }
}
