package com.pdf.generator;

import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.webkit.WebView;

/**
 * Created by cesar on 22/01/2017.
 */

public class PDFPrinter extends PrintDocumentAdapter {

    private PrintDocumentAdapter mWrappedInstance = null;

    public PDFPrinter(WebView webView){
        // change this line with webView.createPrintDocumentAdapter(String), in the future.
        mWrappedInstance = webView.createPrintDocumentAdapter();
    }

    @Override
    public void onStart() {
        mWrappedInstance.onStart();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal, LayoutResultCallback callback,
                         Bundle extras) {
        mWrappedInstance.onLayout(oldAttributes, newAttributes, cancellationSignal,
                callback, extras);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {
        mWrappedInstance.onWrite(pages, destination, cancellationSignal, callback);
    }

    @Override
    public void onFinish() {
        mWrappedInstance.onFinish();
        // Intercept the finish call to know when printing is done
        // and destroy the WebView as it is expensive to keep around.
        //webView.destroy();
        //WebView = null;
    }
}
