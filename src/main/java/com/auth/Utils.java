package com.auth;

public class Utils {
    // Helper method to extract values from the JSON string
    static String extractValue(String json, String key) {
        // Locate the key in the JSON string
        String keyWithQuotes = "\"" + key + "\":\"";
        int startIndex = json.indexOf(keyWithQuotes) + keyWithQuotes.length();
        int endIndex = json.indexOf("\"", startIndex);

        // Extract the value between the key and the next quote
        return json.substring(startIndex, endIndex);
    }
}
