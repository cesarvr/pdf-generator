package com.pdf.generator;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Printer;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;

import java.io.File;
import java.io.IOException;

/**
 * Created by cesar on 22/01/2017.
 */

public class PDFPrinterWebView extends WebViewClient {

    private PrintManager printManager = null;
    private static final String TAG = "PDFPrinterWebView";

    //Cordova Specific, delete this safely if not using cordova.
    private CallbackContext cordovaCallback;
    private Context ctx;

    public PDFPrinterWebView(PrintManager _printerManager, Context ctx){
        printManager = _printerManager;
        this.ctx = ctx;
    }

    public void setCordovaCallback(CallbackContext cordovaCallback){
     this.cordovaCallback = cordovaCallback;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        PrintDocumentAdapter pda = view.createPrintDocumentAdapter("pdf");
        PDFPrinter pdfPrinter = new PDFPrinter(view);

        printManager.print("PDF", pda, null);

        this.cordovaCallback.success("success");
    }

    private ParcelFileDescriptor createTemporaryFile(){
        ParcelFileDescriptor pfd = null;

        // context being the Activity pointer
        File outputDir = this.ctx.getCacheDir();

        try {
            File tempFile = File.createTempFile("pdf-file", "pdf", outputDir);
            pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.e(TAG, e.getMessage());
        }

        return pfd;
    }
}
