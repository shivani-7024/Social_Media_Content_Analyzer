package com.example.socialmediacontentanalyzer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class Image{
    private static final String TAG = "Image";

    public interface Callback {
        void onSuccess(String text);
        void onFailure(String error);
    }

    public void processImage(Context context, Uri uri, Callback callback) {
        try {
            InputImage image = InputImage.fromFilePath(context, uri);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(text -> {
                        Log.d(TAG, "Text recognition successful");
                        callback.onSuccess(text.getText());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Text recognition failed", e);
                        callback.onFailure("Failed to extract text from image.");
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error processing image", e);
            callback.onFailure("Failed to process the image.");
        }
    }
}