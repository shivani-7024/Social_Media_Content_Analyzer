package com.example.socialmediacontentanalyzer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.InputStream;

public class Pdf {
    private static final String TAG = "PdfProcessor";

    public interface Callback {
        void onSuccess(String text);
        void onFailure(String error);
    }

    public void processPdf(Context context, Uri uri, Callback callback) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                callback.onFailure("Failed to open PDF file.");
                return;
            }

            PdfReader reader = new PdfReader(inputStream);
            PdfDocument pdfDocument = new PdfDocument(reader);

            StringBuilder extractedText = new StringBuilder();
            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
                String pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i));
                extractedText.append(pageText).append("\n");
            }

            pdfDocument.close();
            Log.d(TAG, "Text extraction from PDF successful");
            callback.onSuccess(extractedText.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error processing PDF", e);
            callback.onFailure("Failed to extract text from PDF.");
        }
    }
}