package BotHandles;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

import com.auth.ZohoAuth;

public class PostMsg {

    private static final String BOT_UNIQUE_NAME = "testbotwq"; // Replace with your bot unique name
    private static String BOT_OAUTH_TOKEN = ""; // Replace with your OAuth token

    public static void sendMessageToUsers(String userIds, String messageContent) {
    	try {
			ZohoAuth auth = new ZohoAuth(new File("client_details.config"), new File("sendToken.protected"));
//			auth.refreshToken();
			BOT_OAUTH_TOKEN=auth.getAuthToken();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
        try {
            // Create the API URL with the Bot Unique Name
            String apiUrl = "https://cliq.zoho.com/api/v2/bots/" + BOT_UNIQUE_NAME + "/message";

            // Create the URL object
            URL url = new URL(apiUrl);

            // Open a connection to the Zoho Cliq API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Zoho-oauthtoken " + BOT_OAUTH_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");

            // Create the message payload
            JSONObject messageJson = new JSONObject();
            messageJson.put("text", messageContent);
            messageJson.put("userids", userIds); // Comma-separated list of user IDs
            messageJson.put("sync_message", true); // Synchronous message sending

            // Write the JSON payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = messageJson.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully to the users: " + userIds);
                System.out.println(connection.getResponseMessage());
            } else {
                // Read and print the error response if the request failed
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Failed to send message. HTTP Response Code: " + responseCode);
                System.out.println("Error Response: " + response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example usage: Sending a message to multiple users
        String userIds = "vijaysai.g@zohocorp.com"; // Replace with actual user IDs
        String messageContent = "You are an idiot!!";
        sendMessageToUsers(userIds, messageContent);
    }
}
