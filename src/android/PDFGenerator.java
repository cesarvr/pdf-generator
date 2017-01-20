package com.html2pdf.generator;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * This class echoes a string called from JavaScript.
 */
public class PDFGenerator extends CordovaPlugin {

    private final static String APPNAME = "PDFGenerator";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("htmlToPDF")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    public void testingWebkit(final Canvas _canvas) {
        LOG.i(APPNAME, "Starting page rendering...");

        Context ctx = this.cordova.getActivity().getApplicationContext();
        final WebView webview = new WebView(ctx);
        webview.draw(_canvas);
        //webview.loadUrl("http://www.google.org/");

        String summary = "<html><body>You scored <b>192</b> points.</body></html>";
        webview.loadData(summary, "text/html", null);

        Rect rect = new Rect();
        webview.getDrawingRect(rect);


        LOG.i(APPNAME, "Height: ", rect.height());
        LOG.i(APPNAME, "Width: ", rect.width());

        LOG.i(APPNAME, "Finish page rendering");

        LOG.i(APPNAME, "We finish here!!!");
    }


    private void coolMethod(String message, CallbackContext callbackContext) {

        // this.webView.getView().draw();

        final Context ctx = this.cordova.getActivity().getBaseContext();


                PdfDocument document = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1000, 1000, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);

                //testingWebkit(page.getCanvas());

                webView.getView().draw(page.getCanvas());
                document.finishPage(page);

                String pathOfMyApp = cordova.getActivity().getFilesDir().getAbsolutePath();
                File path = getDefaultLocation(pathOfMyApp);

                try {
                    document.writeTo(getOutputStream(path));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("PDFGenerator", e.getMessage());
                    document.close();
                }

                document.close();

                LOG.i(APPNAME, path.getAbsolutePath());
                OpenWith(path);

    }


    private File getDefaultLocation(String path) {
        return new File(path + "/" + "myfile.pdf");
    }

    ;

    private OutputStream getOutputStream(File file) {
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            LOG.d("We Fail miserably", "Salvando fichero");
            e.printStackTrace();
            Log.e(APPNAME, e.getMessage());
        }

        return fs;
    }

    private void info(File file) {
        Log.i(APPNAME, "File size " + file.length() / 1024 + " kb");
        Log.i(APPNAME, "File name " + file.getName());
        Log.i(APPNAME, "File path " + file.getAbsolutePath());
    }

    private void OpenWith(File file) {
        Uri path = Uri.fromFile(file);

        this.info(file);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");

        try {
            this.cordova.getActivity().startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.e(APPNAME, e.getMessage());
        }
    }


}
