package com.pdf.generator;

import android.print.PDFConfig;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.webkit.WebView;

import org.apache.cordova.LOG;

/**
 * Created by cesar on 22/01/2017.
 */

public class PDFPrinter extends PrintDocumentAdapter {

    static final String APPNAME = "PDFPrinter";

    private PrintDocumentAdapter mWrappedInstance = null;
    private WebView webView = null;
    private PrintAttributes attributes = null;
    private PDFConfig config = null;

    public PDFPrinter(WebView webView, String fileName) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.mWrappedInstance = webView.createPrintDocumentAdapter(fileName);
        } else {
            this.mWrappedInstance = webView.createPrintDocumentAdapter();
        }
        config = new PDFConfig();
        this.webView = webView;
    }

    private PrintAttributes configureAttributes(PrintAttributes attrs) {

        LOG.i(APPNAME, "Adding Printer Attributes");
        return new PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(attrs.getResolution())
            .setMinMargins(new PrintAttributes.Margins(5000,10,5000,10)).build();
    }

    @Override
    public void onStart() {
        mWrappedInstance.onStart();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
        CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
      
        mWrappedInstance.onLayout(
            this.configureAttributes(oldAttributes),
            this.configureAttributes(newAttributes),
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
        LOG.i(APPNAME, "Cleaning pdfwriter & webView objects.");
        mWrappedInstance.onFinish();
    }
}
