package BotHandles;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import com.auth.ZohoAuth;

public class ScheduleMessage {

    public static void main(String[] args) {
        try {
        	ZohoAuth auth = new ZohoAuth(new File("client_details.config"),new File("scheduler.protected"));
            String accessToken = auth.getAuthToken();           
            String chatId = "CT_2263886478101278937_64396901-B2"; // Replace with the actual chat ID (either user or channel chat ID)
            String message = "Hello! This is a scheduled message from your bot!";

            // Set scheduled time to 2:40 PM IST (Asia/Kolkata)
            String scheduleTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))
                                               .withHour(14)
                                               .withMinute(48)
                                               .withSecond(0)
                                               .withNano(0)
                                               .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // API endpoint to schedule a message (no chat_id in URL)
            String urlString = "https://cliq.zoho.com/api/v2/scheduledmessages";

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Zoho-oauthtoken " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create the request payload
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("text", message);
            requestPayload.put("schedule_time", scheduleTime);
            requestPayload.put("schedule_status", "scheduled");
            requestPayload.put("schedule_timezone", "Asia/Kolkata");
            requestPayload.put("chat_id", chatId);  // Use chat_id in the body

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestPayload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print response code and message for debugging
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Body: " + response.toString());

            // Check if the message was scheduled successfully
            if (responseCode == 200) {
                System.out.println("Scheduled Message Response: " + response.toString());
            } else {
                System.out.println("Failed to schedule message. Response Code: " + responseCode);
                System.out.println("Response: " + response.toString());
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
