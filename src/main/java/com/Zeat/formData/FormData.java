package com.Zeat.formData;

import com.auth.ZohoAuth;
import food.FoodProcessor;
import food.MainFoodListener;

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
import java.time.LocalTime;
import java.util.ArrayList;

//User Request to Sign up
public class FormData implements Runnable {
	MainFoodListener mainFoodListener = new MainFoodListener();
    private static String accessToken = "";
    private static final UserInserter obj = new UserInserter();
    private static Users user;
    private static ArrayList<Users> userList;
    

    

    public void run() {
        while (true) {
        	try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            	e.printStackTrace();
            }
        	userList = new ArrayList();
            System.out.println("FormData");
            ZohoAuth auth;
            try {
                auth = new ZohoAuth(new File("client_details.config"), new File("Tokens.protected"));
                accessToken = auth.getAuthTokenFirst();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                getDB("formdata");
                getDB("userdata");
                try {
                    for (Users user : userList) {
	                    obj.insertUser(user.getUsername(),user.getChatID(), user.getMail(), user.getHeight(), user.getWeight(), user.getGender(),
	                            user.getAge(), user.getPhysicalActivityLevel(), user.isEggs(), user.getFitnessGoal(),
	                            user.getMeals(), user.getDietaryPreferences(), user.getHealthConditions(), user.getAllergies());
	                    System.out.println("Data inserted successfully!");
                    }
                    LocalTime now = LocalTime.now();
                    FoodProcessor foodProcessor = mainFoodListener.getFoodProcessor();
                    if (now.isBefore(LocalTime.of(7,30))) {
                        mainFoodListener.executeScheduledTask(1);
                    }
                    if (now.isBefore(LocalTime.of(11,30))) {
                        foodProcessor.executeBasedOnMealID(2);
                    }
                    if (now.isBefore(LocalTime.of(3,30))) {
                        foodProcessor.executeBasedOnMealID(3);
                    }
                    if (now.isBefore(LocalTime.of(6,30))) {
                        foodProcessor.executeBasedOnMealID(4);
                    }
                    if (now.isBefore(LocalTime.of(23,30))) {
                        mainFoodListener.executeScheduledTask(3);
                    }
                } catch (SQLException e) {
                    boolean padma = false;
                    try {
                        padma = obj.tableMapper.prepareAndReadMap("SELECT * FROM user WHERE EmailId = ?", user.getMail()).isEmpty();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    if (e.getMessage().contains("Duplicate entry") || padma) {
                    	
                        System.err.println("User : "+user.getMail()+" Tried to sign up again");
                    } else {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.err.println("No Users");
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
            user = new Users();
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
            boolean snack = (boolean) record.get("snacks");
            if (snack) {
                user.setMeals(user.getMeals().toString() + ",Snack");
            }
            userList.add(user);
            DeleteDBdata(record.getString("id"), "formdata");
        }
    }

    public static void getDbData(StringBuilder response) {
        JSONObject obj = new JSONObject(response.toString());
        JSONArray list = obj.getJSONArray("list");

        for (int i = 0; i < list.length(); i++) {
        	user = userList.get(i);
            JSONObject record = (JSONObject) list.getJSONObject(i);
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

