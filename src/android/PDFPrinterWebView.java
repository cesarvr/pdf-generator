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

        PDFPrinter pdfPrinter = new PDFPrinter(view);

        printManager.print("PDF", pdfPrinter, null);

        this.cordovaCallback.success("success");
    }

}
