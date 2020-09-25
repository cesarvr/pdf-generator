package com.pdf.generator;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.print.PDFtoBase64;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;

import java.util.HashMap;

/**
 * Created by cesar on 22/01/2017.
 */

public class PDFPrinterWebView extends WebViewClient {
    private static final String TAG = "PDFPrinterWebView";
    private static final String PRINT_JOB = "PDFCordovaPlugin";
    private static final String PRINT_SUCCESS = "success";

    private PrintManager printManager;
    private CallbackContext cordovaCallback;
    private Context ctx;
    private boolean outputBase64;
    private String fileName;
    private String orientation;
    private PrintAttributes.MediaSize pageType;
    private HashMap<String, PrintAttributes.MediaSize> pageOptions = new HashMap<>();

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
        if (pageType == null) {
            pageType = pageOptions.get("A4");
        }
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
        if (!"landscape".equalsIgnoreCase(this.orientation)) {
            mediaSize = pageType.asPortrait();
        }

        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(mediaSize)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(new PrintAttributes.Margins(10, 10, 10, 5)).build();

        PrintDocumentAdapter printAdapter;

        if (this.outputBase64) {
            LOG.d(TAG, "creating a new WebView print adapter.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                printAdapter = webView.createPrintDocumentAdapter(fileName);
            } else {
                printAdapter = webView.createPrintDocumentAdapter();
            }

            LOG.d(TAG, "generating a base64 representation of the PDF");
            PDFtoBase64 pdfToBase64 = new PDFtoBase64(attributes, this.ctx, this.cordovaCallback, webView);
            pdfToBase64.process(printAdapter);

        } else {
	        LOG.d(TAG, "creating a new print job.");
            PDFPrinter pdfPrinterAdapter = new PDFPrinter(webView, fileName);
            printManager.print(PRINT_JOB, pdfPrinterAdapter, attributes);
            this.cordovaCallback.success(PRINT_SUCCESS);
        }
    }
}
