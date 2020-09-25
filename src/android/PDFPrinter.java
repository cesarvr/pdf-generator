package com.pdf.generator;

import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.webkit.WebView;

import org.apache.cordova.LOG;

/**
 * Created by cesar on 22/01/2017.
 */

public class PDFPrinter extends PrintDocumentAdapter {
    private static final String LOG_TAG = "PDFPrinter";
    private final WebView webView;
    private PrintDocumentAdapter mWrappedInstance;

    public PDFPrinter(WebView webView, String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mWrappedInstance = webView.createPrintDocumentAdapter(fileName);
        } else {
            this.mWrappedInstance = webView.createPrintDocumentAdapter();
        }
        this.webView = webView;
    }

    @Override
    public void onStart() {
        mWrappedInstance.onStart();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

        mWrappedInstance.onLayout(
                oldAttributes,
                newAttributes,
                cancellationSignal,
                callback,
                extras
        );
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {
        mWrappedInstance.onWrite(pages, destination, cancellationSignal, callback);
    }

    @Override
    public void onFinish() {
        LOG.d(LOG_TAG, "Cleaning pdfwriter & webView objects.");
        mWrappedInstance.onFinish();
        webView.destroy();
    }
}
