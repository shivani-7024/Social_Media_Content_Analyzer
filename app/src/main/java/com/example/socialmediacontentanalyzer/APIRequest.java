package com.example.socialmediacontentanalyzer;

import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIRequest {
    private static final String TAG = "NetworkRequest";
    private static final String API_KEY = "YOUR API KEY";

    public void sendTextForProcessing(Context context, String text, TextView textView, LinearLayout text_card, LinearLayout launcher, LottieAnimationView loader) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
        text = text + "Suggest me What should I improve in this\n" +
                "Response Type is comes with html tags no other things only with html tags," +
                " don't recommend html tags use it only for formating the data " +
                "when i give you image only that condition suggest me tranding has tags not in any other case\n"
                +"when you are giving your response don't recommends the conclusion or any other irrelevant response in the end ";

        try {
            JSONObject textPart = new JSONObject();
            textPart.put("text", text);

            JSONArray partsArray = new JSONArray();
            partsArray.put(textPart);

            JSONObject contentObject = new JSONObject();
            contentObject.put("parts", partsArray);

            JSONArray contentsArray = new JSONArray();
            contentsArray.put(contentObject);

            JSONObject requestData = new JSONObject();
            requestData.put("contents", contentsArray);

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                    response -> {
                        try {
                            JSONArray candidatesArray = response.optJSONArray("candidates");
                            if (candidatesArray != null && candidatesArray.length() > 0) {
                                JSONObject candidateContentObject = candidatesArray.optJSONObject(0).optJSONObject("content");
                                if (candidateContentObject != null) {
                                    JSONArray candidatePartsArray = candidateContentObject.optJSONArray("parts");
                                    if (candidatePartsArray != null && candidatePartsArray.length() > 0) {
                                        String textContent = candidatePartsArray.optJSONObject(0).optString("text", "");

                                        // Use HtmlCompat to render HTML content in the TextView

                                        textContent = textContent.replace("```", "").replace("html", "")
                                                .replace("*", "\n")
                                                .replace(":", "\n");

                                        textView.setText(HtmlCompat.fromHtml(textContent, HtmlCompat.FROM_HTML_MODE_LEGACY));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            textView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                                        }
                                        text_card.setVisibility(View.VISIBLE);
                                        loader.setVisibility(View.GONE);
                                        launcher.setVisibility(View.GONE);

                                        Log.d("Api response", textContent);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing response", e);
                            textView.setText("Error processing response.");
                        }
                    },
                    error -> {
                        Log.e(TAG, "Request failed", error);
                        textView.setText("Error: " + error.getMessage());
                    }
            );

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,  // Timeout in milliseconds (30 seconds)
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,  // Number of retries
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT  // Backoff multiplier
            ));

            queue.add(jsonObjectRequest);

        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON request", e);
            textView.setText("Exception: " + e.getMessage());
        }
    }
}
