package com.pdf.generator;

import android.os.Build;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.cordova.CallbackContext;

/**
 * Created by cesar on 22/01/2017.
 */

public class PDFPrinterWebView extends WebViewClient {

    private PrintManager printManager = null;

    //Cordova Specific, delete this safely if not using cordova.
    private CallbackContext cordovaCallback;

    public PDFPrinterWebView(PrintManager _printerManager){
        printManager = _printerManager;
    }

    public void setCordovaCallback(CallbackContext cordovaCallback){
     this.cordovaCallback = cordovaCallback;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        PDFPrinter pdfPrinter = new PDFPrinter(view);
        printManager.print("PDF",pdfPrinter, null);

        this.cordovaCallback.success("success");
    }
}
