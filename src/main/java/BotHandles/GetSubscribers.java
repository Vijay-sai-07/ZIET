package BotHandles;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import com.auth.ZohoAuth;

public class GetSubscribers {

    private static final String BOT_UNIQUE_NAME = "testbotwq";  // Replace with your bot's unique name
    private static  String BOT_OAUTH_TOKEN = "";  // Replace with your bot's OAuth token
    private static String sync_token="";
    private static final String propertiesFilePath = "sync_token.protected";

    public static void main(String[] args) {
    	try {
			ZohoAuth auth = new ZohoAuth(new File("client_details.config"), new File("client2.protected"));
//			auth.refreshToken();
			BOT_OAUTH_TOKEN=auth.getAuthToken();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
        try {
            // URL for the GET request
        	fetch_sync_token();
        	String url="";
        	if(sync_token.isEmpty()) {
                url = "https://cliq.zoho.com/api/v2/bots/" + BOT_UNIQUE_NAME + "/subscribers";
        	}
        	else {
                url = "https://cliq.zoho.com/api/v2/bots/" + BOT_UNIQUE_NAME + "/subscribers?sync_token=" + sync_token;
        	}

            sendGetRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendGetRequest(String url) throws IOException {
        // Create URL object
        URL obj = new URL(url);

        // Open connection
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        // Set HTTP method to GET
        connection.setRequestMethod("GET");

        // Set Authorization header (Zoho OAuth token)
        connection.setRequestProperty("Authorization", "Zoho-oauthtoken " + BOT_OAUTH_TOKEN);

        // Get the HTTP response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // If the response is OK, read and print the response
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // Read the response line by line
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject object=new JSONObject(response.toString());
            
            String new_sync_token=object.getString("sync_token");
            writeSyncToken(new_sync_token);
            JSONArray records = object.getJSONArray("data");
            for(Object record : records) {
            	System.out.println(record);
            }
            
            
//            System.out.println(response);

        } else {
            System.out.println("GET request failed. Response Code: " + responseCode);
        }
    }
    
    public static void writeSyncToken(String new_sync_token) {
    	
    	 Properties properties = new Properties();

         // Add key-value pairs to the properties object
         properties.setProperty("sync_token", new_sync_token);

         // Save the properties to the file (it will overwrite the existing file)
         try (FileOutputStream fos = new FileOutputStream(propertiesFilePath)) {
             // Write properties to file with a comment header
             properties.store(fos, "Sync Token");

             System.out.println("Properties file overwritten successfully.");
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
    public static String fetch_sync_token() {
    	    	        
    	 Properties p = new Properties();
         try {
			p.load(new FileReader(propertiesFilePath));
	         sync_token = p.getProperty("sync_token");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         return sync_token;
    }
}
