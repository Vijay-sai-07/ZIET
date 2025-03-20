package user.utils;

import java.util.Base64;
import org.json.JSONObject;

public class Base64Decrypter {

    public static void main(String args) {
        // Example JWT token (replace with the actual token)
        String jwt = args; // Replace with the actual ID token received from Zoho

        // Split the JWT into three parts: Header, Payload, and Signature
        String[] jwtParts = jwt.split("\\.");

        // Decode the Header and Payload (Base64Url decode)
        String header = decodeBase64Url(jwtParts[0]);
        String payload = decodeBase64Url(jwtParts[1]);

        // Output the decoded JSON content (Header and Payload)
        System.out.println("Decoded Header: " + header);
        System.out.println("Decoded Payload: " + payload);

        // Convert Payload into JSON for easy access to user details
        JSONObject payloadJson = new JSONObject(payload);

        System.out.println(payloadJson);

        // Access user details (example: sub, name, email, etc.)
        String userId = payloadJson.optString("sub");
        String userEmail = payloadJson.optString("email");
        String userName = payloadJson.optString("name");

        System.out.println("manager.User ID: " + userId);
        System.out.println("manager.User Email: " + userEmail);
        System.out.println("manager.User Name: " + userName);
    }

    // Method to decode Base64Url encoded string
    public static String decodeBase64Url(String encoded) {
        // Base64Url uses '-' instead of '+', and '_' instead of '/'
        String base64 = encoded.replace('-', '+').replace('_', '/');

        // Add padding if necessary
        while (base64.length() % 4 != 0) {
            base64 += "=";
        }

        // Decode using Base64
        byte[] decodedBytes = Base64.getDecoder().decode(base64);

//        System.out.println(new String(decodedBytes));

        return new String(decodedBytes);
    }
}
