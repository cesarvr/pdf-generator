package com.pdf.generator;

import android.content.Context;
import android.print.PrintManager;
import android.util.Log;
import android.webkit.WebView;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This plugin creates a PDF from a given HTML website or string
 */
public class PDFGenerator extends CordovaPlugin {
    private final static String LOG_TAG = "PDFGenerator";

    public WebView getOffscreenWebkitInstance(Context ctx) {
        LOG.d(LOG_TAG, "Mounting offscreen WebView");
        WebView view = new WebView(ctx);
        view.getSettings().setDatabaseEnabled(true);
        view.getSettings().setJavaScriptEnabled(true);
        return view;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if ("htmlToPDF".equals(action)) {
            this.pdfPrinter(args, callbackContext);
            return true;
        }
        return false;
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
    }

    private void pdfPrinter(final JSONArray args, final CallbackContext callbackContext) {
        final Context ctx = this.cordova.getActivity().getApplicationContext();

        cordova.getActivity().runOnUiThread(() -> {
            try {
                WebView webview = getOffscreenWebkitInstance(ctx);

                PrintManager printManager = (PrintManager) cordova.getActivity()
                        .getSystemService(Context.PRINT_SERVICE);

                boolean outputBase64 = args.getString(4) != null && args.getString(4).equals("base64");
                PDFPrinterWebView printerWebView = new PDFPrinterWebView(printManager, ctx, outputBase64);

                String fileNameArg = args.getString(5);
                if (fileNameArg != null) {
                    printerWebView.setFileName(fileNameArg);
                }

                String pageType = args.getString(2);
                printerWebView.setPageType(pageType);

                String orientation = args.getString(3);
                if (orientation != null) {
                    printerWebView.setOrientation(orientation);
                }

                printerWebView.setCordovaCallback(callbackContext);
                webview.setWebViewClient(printerWebView);

                if (args.getString(0) != null && !args.getString(0).equals("null"))
                    webview.loadUrl(args.getString(0));

                if (args.getString(1) != null && !args.getString(1).equals("null"))
                    webview.loadDataWithBaseURL(null, args.getString(1), "text/HTML", "UTF-8", null);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Unable to parse JSON", e);
                callbackContext.error("Native parsing arguments: " + e.getMessage());
            }
        });
    }
}
