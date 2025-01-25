package com.example.socialmediacontentanalyzer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {
    private TextView extractedTextView;
    private APIRequest networkRequest;
    private LottieAnimationView  loader;
    private LinearLayout text_card;
    private ImageView copy_text, back;
    private LinearLayout pickFileButton;
    private boolean doubleBackToExitPressedOnce = false;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleSelectedFile);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        extractedTextView = findViewById(R.id.text);
        pickFileButton = findViewById(R.id.button);
        text_card = findViewById(R.id.text_card);
        copy_text = findViewById(R.id.copy_text);
        back = findViewById(R.id.back);
        loader = findViewById(R.id.loader);

        networkRequest = new APIRequest();

        pickFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePickerLauncher.launch("*/*");
                pickFileButton.setVisibility(View.GONE);
                text_card.setVisibility(View.GONE);
                loader.setVisibility(View.GONE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_card.setVisibility(View.GONE);
                pickFileButton.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);
            }
        });

        copy_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyTextToClipboard();
            }
        });
    }

    private void handleSelectedFile(Uri uri) {
        if (uri == null) {
            showDialog("No file selected");
            return;
        }

        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                new Image().processImage(this, uri, new Image.Callback() {
                    @Override
                    public void onSuccess(String text) {
                        loader.setVisibility(View.VISIBLE);
                        networkRequest.sendTextForProcessing(MainActivity.this, text, extractedTextView, text_card, pickFileButton, loader);
                    }

                    @Override
                    public void onFailure(String error) {
                        showToast(error);
                    }
                });
            } else if (mimeType.equals("application/pdf")) {
                new Pdf().processPdf(this, uri, new Pdf.Callback() {
                    @Override
                    public void onSuccess(String text) {
                        loader.setVisibility(View.VISIBLE);
                        networkRequest.sendTextForProcessing(MainActivity.this, text, extractedTextView, text_card, pickFileButton, loader);
                    }

                    @Override
                    public void onFailure(String error) {
                        showToast(error);
                    }
                });
            } else {
                showDialog("Unsupported file type. Please select an image or a PDF.");
            }
        } else {
            showDialog("Could not determine file type.");
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
        text_card.setVisibility(View.GONE);
        loader.setVisibility(View.GONE);
        pickFileButton.setVisibility(View.VISIBLE);
    }

    private void showToast(String message) {
        text_card.setVisibility(View.GONE);
        loader.setVisibility(View.GONE);
        pickFileButton.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void copyTextToClipboard() {
        String textToCopy = extractedTextView.getText().toString();
        if (!textToCopy.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No text to copy", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}
