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
import android.util.Log;

import java.io.File;
import java.lang.System;
import java.util.HashMap;

import android.print.PDFtoBase64;
import android.print.PrintAttributes;
import android.os.Environment;

/**
 * Created by cesar on 22/01/2017.
 */

public class PDFPrinterWebView extends WebViewClient {

    private PrintManager printManager = null;
    private static final String TAG = "PDFPrinterWebView";
    private static final String PRINT_JOB = "PDFCordovaPlugin"; 
    private static final String PRINT_SUCESS = "sucess";

    //Cordova Specific, delete this safely if not using cordova.
    private CallbackContext cordovaCallback;
    private Context ctx;
    private boolean outputBase64;

    private String fileName;
    private String orientation;
    PrintAttributes.MediaSize pageType;

    HashMap<String, PrintAttributes.MediaSize> pageOptions = new HashMap<String, PrintAttributes.MediaSize>();



    public PDFPrinterWebView(PrintManager _printerManager, Context ctx, Boolean outputBase64) {
        printManager = _printerManager;
        this.ctx = ctx;
        this.outputBase64 = outputBase64;

        pageOptions.put("A3", PrintAttributes.MediaSize.ISO_A3);
        pageOptions.put("A4", PrintAttributes.MediaSize.ISO_A4);
        pageOptions.put("A2", PrintAttributes.MediaSize.ISO_A2);
        pageOptions.put("A1", PrintAttributes.MediaSize.ISO_A1);
    }

    public void setPageType(String type) {
        pageType = pageOptions.get(type);
        if(pageType == null) pageType = pageOptions.get("A4");
    }

    public void setCordovaCallback(CallbackContext cordovaCallback) {
        this.cordovaCallback = cordovaCallback;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);

        PrintAttributes.MediaSize mediaSize = pageType.asLandscape();
        if(!this.orientation.equalsIgnoreCase("landscape")) {
            mediaSize =  pageType.asPortrait();
        }

        PrintAttributes attributes = new PrintAttributes.Builder()
            .setMediaSize(mediaSize)
            .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
            .setMinMargins(new PrintAttributes.Margins(10,10,10,5)).build();
            //.setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();


        PrintDocumentAdapter printAdapter = null;

        Log.e(TAG, "creating a new WebView adapter.");
        if (Build.VERSION.SDK_INT >= 21) {
            printAdapter = webView.createPrintDocumentAdapter(fileName);
        } else {
            printAdapter = webView.createPrintDocumentAdapter();
        }


        if (this.outputBase64) {
            Log.e(TAG, "generating a base64 representation of the PDF");
            PDFtoBase64 pdfToBase64 = new PDFtoBase64(attributes, this.ctx, this.cordovaCallback);
            pdfToBase64.process(printAdapter);

        } else {

            PDFPrinter pdfPrinterAdapter = new PDFPrinter(webView, fileName);
            Log.e(TAG, "creating a new print job.");
            printManager.print(PRINT_JOB, pdfPrinterAdapter, attributes );

            this.cordovaCallback.success(PRINT_SUCESS);
        }
    }

}
