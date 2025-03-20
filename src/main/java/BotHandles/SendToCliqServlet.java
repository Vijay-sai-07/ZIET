package BotHandles;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;

@WebServlet("/sendToCliq")
public class SendToCliqServlet extends HttpServlet {

    // Replace this with your actual Incoming Webhook URL
    private static final String CLIQ_WEBHOOK_URL = "https://cliq.zoho.com/company/64396901/api/v2/bots/testbotwq/incoming"; // Change this to your real webhook URL

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Fetch data from your source (like database, or a sample data)
        JSONObject jsonData = fetchDataFromYourSource();

        // Send this data to Zoho Cliq using the Incoming Webhook URL
        sendMessageToCliq(jsonData);

        // Respond to the client with a success message
        response.getWriter().write("Data sent to Cliq successfully.");
    }

    private JSONObject fetchDataFromYourSource() {
        // Sample data for now, but you can fetch actual data (like from a database or API)
        JSONObject json = new JSONObject();
        json.put("status", "success");
        json.put("message", "Here's some important data from your servlet!");
        return json;
    }

    private void sendMessageToCliq(JSONObject data) {
        try {
            // Prepare the message content to be sent to Zoho Cliq
            JSONObject message = new JSONObject();
            message.put("text", "Message from your bot: " + data.toString());

            // Establish connection to the Zoho Cliq Incoming Webhook URL
            URL url = new URL(CLIQ_WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Write the JSON message to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = message.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response from Zoho Cliq and handle it
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent to Cliq successfully.");
            } else {
                System.out.println("Failed to send message. HTTP Response Code: " + responseCode);
            }

            // Optionally, read the response content (if any)
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                System.out.println("Response from Cliq: " + content.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
