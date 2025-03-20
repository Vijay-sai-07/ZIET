package com.Zeat.formData;

import com.auth.ZohoAuth;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

//User Request to Sign up
public class FormData implements Runnable {

    private static String accessToken = "";
    private static final UserInserter obj = new UserInserter();
    private static Users user;
    private static boolean snack = false;


    public void run() {
        while (true) {
            user = new Users();
            System.out.println("FormData");
            ZohoAuth auth;
            try {
                auth = new ZohoAuth(new File("client_details.config"), new File("Tokens.protected"));
                accessToken = auth.getAuthToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                getDB("formdata");
                getDB("userdata");
                try {
                    if (snack) {
                        user.setMeals(user.getMeals().toString() + ",Snack");
                    }
                    obj.insertUser(user.getUsername(),user.getChatID(), user.getMail(), user.getHeight(), user.getWeight(), user.getGender(),
                            user.getAge(), user.getPhysicalActivityLevel(), user.isEggs(), user.getFitnessGoal(),
                            user.getMeals(), user.getDietaryPreferences(), user.getHealthConditions(), user.getAllergies());
                    System.out.println("Data inserted successfully!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.err.println("No Users");
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            	e.printStackTrace();
            }
        }
    }

    public static void getDB(String DbName) throws IOException {
        String apiUrl = "https://cliq.zoho.com/api/v2/storages/" + DbName + "/records";
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Zoho-oauthtoken " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        int responseCode = conn.getResponseCode();
        StringBuilder response = null;
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            if (DbName.equals("formdata")) {
                getData(response);
            } else {
                getDbData(response);
            }

        } else {
            System.out.println(responseCode);
            System.out.println("Failed to fetch records.");
        }
    }

    public static void getData(StringBuilder response) {
        JSONObject obj = new JSONObject(response.toString());
        JSONArray list = obj.getJSONArray("list");

        for (Object x : list) {
            JSONObject record = (JSONObject) x;

            user.setChatID(record.getString("id"));
            if (record.getString("allergies").equals("")) {
                user.setAllergies("NONE");

            } else {
                user.setAllergies(record.getString("allergies"));

            }
            user.setEggs(record.getBoolean("eggs"));
            user.setWeight(record.getDouble("weight"));
            user.setHealthConditions(record.getString("hs"));
            user.setDietaryPreferences(record.getString("dp"));
            user.setMail(record.getString("uid"));
            user.setFitnessGoal(record.getString("fitness"));
            user.setPhysicalActivityLevel(record.getString("pal"));
            user.setAge(record.getInt("age"));
            user.setMeals(record.getString("meals"));
            user.setDietaryPreferences(record.getString("dp"));
            user.setHealthConditions(record.getString("hs"));
            user.setAllergies(record.getString("allergies"));
            user.setHeight(record.getDouble("height"));
            snack = (boolean) record.get("snacks");

            DeleteDBdata(record.getString("id"), "formdata");
        }
    }

    public static void getDbData(StringBuilder response) {
        JSONObject obj = new JSONObject(response.toString());
        JSONArray list = obj.getJSONArray("list");

        for (Object x : list) {
            JSONObject record = (JSONObject) x;
            String gender = record.getString("gender");
            user.setChatID(record.getString("id"));
            user.setUsername(record.getString("username"));
            user.setMail(record.getString("mail"));
            user.setGender(record.getString("gender"));

            DeleteDBdata(record.getString("id"), "userdata");
        }
    }


    public static void DeleteDBdata(String recordID, String DBname) {

        String apiUrl = "https://cliq.zoho.com/api/v2/storages/" + DBname + "/records/" + recordID;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 204) {
                System.out.println("Record deleted successfully!" + recordID);
            } else {
                System.out.println("Failed to delete record. HTTP Response Code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



   



