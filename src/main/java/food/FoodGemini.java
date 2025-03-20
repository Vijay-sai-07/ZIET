package food;

import data_reader.DBColumnWiseReader;
import database_handler.DBConnector;
import gemini.GeminiApi;
import observations.ObsLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoodGemini {
    private final GeminiApi gemini = GeminiApi.getGemini();
    private final DBColumnWiseReader dbColumnWiseReader = new DBColumnWiseReader(DBConnector.publicConnection);
    private final String foodRetrievingQuery = "SELECT CONCAT(f.FoodName, ':', f.Calories, ' kcal per ', f.Quantity_value, ' ', f.Quantity_type, ' ', m.meal) AS food_details FROM food_items f JOIN meal m ON f.mealID = m.mealID WHERE f.mealID = ?";
    public static final String[] meals = {
            "BreakFast", "Lunch", "Snack", "Dinner", "Midnight_Dinner"
    };

    public void storeMealRecommendationForUser(ArrayList<Map<String, ?>> users, int mealId) {
        if (users == null || users.isEmpty()) {
            System.out.println("NO USERS");
            return;
        }
        storeResponsesToDB(users, retrieveFoods(mealId), mealId);
    }

    private String extractJson(String input) {
        // Regex to match content inside triple single quotes OR triple backticks
        input = input.replaceAll("\n","");
//        input = input.replaceAll(" ","");
        Pattern pattern = Pattern.compile("['`]{3}json(.*?)['`]{3}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String jsonString = matcher.group(1).trim(); // Extract JSON
            System.out.println("Extracted JSON:\n" + jsonString); // Debugging
            return jsonString; // Return formatted JSON
        }
        System.out.println("No JSON found in input.");
        return "null";
    }

    private ArrayList<String> retrieveFoods(long mealId) {
        ArrayList<String> foodDetails = null;
        long startTime = System.nanoTime();
        try {
            foodDetails = (ArrayList<String>) dbColumnWiseReader.prepareAndReadColumns(foodRetrievingQuery, mealId).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        ObsLogger.logObservation("sql retrieval time: " + duration);
        return foodDetails;
    }

    private void storeResponsesToDB(ArrayList<Map<String, ?>> users, ArrayList<String> foodDetails, int mealId) {
        for (Map<String, ?> user : users) {
            long startTime1 = System.nanoTime();
            String meal = meals[mealId - 1];
            String response = gemini.getResponseAsString("These are my health details " + user + ". These are the only foods available to me right now " + foodDetails + "Give me a " + meal + " meal plan of STRICTLY "+user.get("Calories_per_meal")+" calories per meal and requirements. I want the output STRICTLY as json of meal_plan array of food_item objects with prior quantity_value (int) and quantity_type (nos, gms) and food_description <what nutrients that food is rich in, etc within 10 words> (string). Finally i want a meal_description <what nutrients that meal is rich in, etc> (string), total_calories <sum of all foods calories> (float), total_proteins <total proteins in meal in gms> (float), total_carbohydrates <total carbohydrates in meal in gms> (float), total_fats <total fats in meal in gms> (float), total_fibres <total fibres in meal in gms> (float). You can give approximate values for nutrient values. Change the gram quantity foods to cups for better understandability."); // format - {"+meals[mealId]+":[{\"food\"}:<food>,\"quantity\":<quantity>,\"calories\":<total calories of food>],\"totalCalories\":<kcal>\"totalCarbs\":<carb(g)>\"totalProteins\":<protein(g)>\"totalFat\":<fat(g)>\"totalFiber\":<fiber(g)>}
            try {
                dbColumnWiseReader.prepareAndExecute("INSERT INTO user_responses (User_id, response, mealID) VALUES  (?, ?, ?)", user.get("User_id"), extractJson(response), mealId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            long endTime1 = System.nanoTime();
            long duration1 = endTime1 - startTime1;
            ObsLogger.logObservation("Gemini Response Time: " + duration1);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
