package gemini;

import com.google.gson.Gson;
import observations.ObsLogger;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GeminiApi {
    private static GeminiApi gemini;
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBkwbZpml9jdZGH51ZXsl6n-LG8C9F6PVc";

    private final OkHttpClient client;

    private GeminiApi() {
        // Set timeouts to prevent indefinite waiting
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public static GeminiApi getGemini() {
        return (gemini == null) ? gemini = new GeminiApi() : gemini;
    }

    public String getResponseAsString(String prompt) {
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Response response = getResponse(prompt);
                if (response != null && response.body() != null) {
                    String responseBody = response.body().string();
                    ResponseData responseData = new Gson().fromJson(responseBody, ResponseData.class);
                    if (responseData != null && responseData.candidates != null && !responseData.candidates.isEmpty()) {
                        String stringResponse = responseData.candidates.get(0).content.parts.get(0).text;
                        ObsLogger.logResults(stringResponse);
                        return stringResponse;
                    }
                }
            } catch (IOException e) {
                System.err.println("Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt == maxRetries) {
                    return "API request failed after " + maxRetries + " attempts.";
                }
            }
        }
        return "Unexpected error occurred.";
    }

    private Response getResponse(String prompt) throws IOException {
        ObsLogger.logPrompts(prompt);
        String promptJson = "{\"contents\": [{ \"parts\": { \"text\": \"" + prompt + "\" } }] }";
        RequestBody requestBody = RequestBody.create(promptJson, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build();

        return client.newCall(request).execute();
    }

    static class ResponseData {
        List<Candidate> candidates;
    }

    static class Candidate {
        Content content;
    }

    static class Content {
        List<Part> parts;
    }

    static class Part {
        String text;
    }
}
