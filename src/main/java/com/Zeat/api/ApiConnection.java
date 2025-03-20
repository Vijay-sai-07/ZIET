package com.Zeat.api;

import com.auth.ZohoAuth;
import data_reader.DBColumnWiseReader;
import data_reader.TableMapper;
import database_handler.DBConnector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class ApiConnection implements Runnable {
    public void run() {
        try {
//            ZohoAuth auth = new ZohoAuth(new File("PadmaCreatorFormToken.config"), new File("getRecordsFromCreator.protected"));
        	ZohoAuth auth = new ZohoAuth(new File("client_details.config"), new File("Tokens.protected"));
            String accessToken = auth.getAuthTokenFirst();
            String ownerName = "zohointranet";
            String appName = "smart-canteen";
            String formName = "Jeeves_Cliq_Menu_Details_Chennai";
            String url = "https://www.zohoapis.com/creator/v2.1/data/" + ownerName + "/" + appName + "/report/" + formName;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Zoho-oauthtoken " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            DBInsertions.deleteData();
            int responseCode = response.statusCode();
            String responseBody = response.body();
            JSONObject myResponse = new JSONObject(responseBody);
            System.out.println(myResponse);
            JSONArray records = myResponse.getJSONArray("data");
            System.out.println(records);
            ArrayList<String> allFoods = (ArrayList<String>) new DBColumnWiseReader(DBConnector.publicConnection).readColumns("SELECT AFoodName FROM all_foods").get(0);
            DBInsertions dbInsertions = new DBInsertions(allFoods.subList(1,allFoods.size()));
            for (Object meal : records) {
                JSONObject mea = (JSONObject) meal;
                String myMenuItems = mea.getString("Cliq_Menu_Items");
                String mealType = mea.getString("Session");
                JSONArray plan = mea.getJSONArray("Plan");
                for (Object cal : plan) {
                    System.out.println(cal);
                    JSONObject calories = (JSONObject) cal;
                    String cals = calories.getString("Calories_Display");
                    JSONObject itemArray = calories.getJSONObject("Food_Item");
                    String foodItem = itemArray.getString("Food_Item");
                    if (foodItem.isEmpty()) {
                        foodItem = itemArray.getString("Gofrugal_Item_Name");
                        if (foodItem.endsWith(" KIT")) {
                            foodItem = foodItem.substring(0, foodItem.length() - 4);
                        }
                    }
                    System.out.println(foodItem);
                    String[] splits = cals.split("\\$\\$");
                    String quantityType = "";
                    BigDecimal quantityValue = BigDecimal.ZERO;
                    String contains = "";
                    int c = 0;
                    String value = "";
                    for (int i = 0; i < 3; i++) {
                        if (splits.length != 1 && i < splits.length) {
                            if (i == 0) {
                                String[] qty = splits[0].split(" ");
                                if (!qty[1].toLowerCase().equalsIgnoreCase("NULL")) {
                                    quantityType = qty[1].toLowerCase();

                                } else {
                                    quantityType = "noCal";

                                }
                                quantityValue = new BigDecimal(qty[0]);
                            } else if (i == 1) {
                                String[] calorie = splits[1].split(" ");
                                c = Integer.parseInt(calorie[0].substring(0, calorie[0].length() - 3));
                            } else if (i == 2) {
                                String[] cont = splits[2].split(" ");
                                contains = cont[1];
                                if (foodItem.equalsIgnoreCase("carrot")) {
                                    value = "null";
                                } else {
                                    for (String val : cont) {
                                        if (!cont[0].equals(val)) {
                                            value += val + " ";
                                        }
                                    }
                                }
                            }
                        } else {
                            if (i == 0) {
                                quantityType = "noCal";
                                quantityValue = BigDecimal.ZERO;
                            } else if (i == 1) {
                                c = 0;
                            } else if (i == 2) {
                                value = "null";
                            }
                        }
                    }
                    String[] foodItems = {
                            "Mid Night Fruit",
                            "Mid Night Juice",
                            "Mid Night Dinner",
                            "Dinner",
                            "Live Snacks",
                            "Floor Snacks",
                            "Annalakshmi Lunch",
                            "Zoho Lunch",
                            "Breakfast",
                            "Porridge"
                    };
                    boolean isPresent = contains(foodItems, mealType);
                    if (isPresent && !(foodItem.equalsIgnoreCase("cds") || foodItem.equalsIgnoreCase("sidedish") || foodItem.equalsIgnoreCase("cms") || foodItem.equalsIgnoreCase("cbs"))) {
                        dbInsertions.insertData(mealType, toTitleCase(foodItem), quantityType, quantityValue, c, value);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to check if the string is in the array
    public static boolean contains(String[] array, String value) {
        for (String item : array) {
            if (item.equals(value)) {
                return true;  // Item found
            }
        }
        return false;  // Item not found
    }
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1))
                         .append(" ");
            }
        }
        return titleCase.toString().trim().strip();
    }

}