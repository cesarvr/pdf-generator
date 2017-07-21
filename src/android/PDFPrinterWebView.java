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
import java.lang.System;

import android.print.PDFtoBase64;
import android.print.PrintAttributes;
import android.os.Environment;

/**
 * Created by cesar on 22/01/2017.
 */

public class PDFPrinterWebView extends WebViewClient {

    private PrintManager printManager = null;
    private static final String TAG = "PDFPrinterWebView";
    private static final String PRINT_JOB_NAME = "PDF_GENERATOR";
    private static final String PRINT_SUCESS = "sucess";

    //Cordova Specific, delete this safely if not using cordova.
    private CallbackContext cordovaCallback;
    private Context ctx;
    private boolean outputBase64;

    private String fileName;

    public PDFPrinterWebView(PrintManager _printerManager, Context ctx, Boolean outputBase64){
        printManager = _printerManager;
        this.ctx = ctx;
        this.outputBase64 = outputBase64;
    }

    public void setCordovaCallback(CallbackContext cordovaCallback){
        this.cordovaCallback = cordovaCallback;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        if(this.outputBase64){
            PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
            PDFtoBase64 pdfToBase64 = new PDFtoBase64(attributes, this.ctx, this.cordovaCallback);
            if(Build.VERSION.SDK_INT >= 21 ){
                pdfToBase64.process(webView.createPrintDocumentAdapter(PRINT_JOB_NAME));
            } else {
                pdfToBase64.process(webView.createPrintDocumentAdapter());
            }
        } else {
            PDFPrinter pdfPrinter = new PDFPrinter(webView, fileName);
            printManager.print(PRINT_JOB_NAME, pdfPrinter, null);
            this.cordovaCallback.success(PRINT_SUCESS);
        }
    }

}
