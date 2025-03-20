package servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.SQLException;

import static servlet.ServletUtil.tableMapper;

public class UserUpdater {

    public void updateUser(int userId, String username, String mail, double height, double weight, String physicalActivityLevel) throws SQLException {
        String updateUserQuery = "UPDATE user SET username = ?, EmailId = ?, height = ?, weight = ?, physical_activity_level = ? WHERE User_id = ?";
        tableMapper.prepareAndExecute(updateUserQuery, username, mail, height, weight, physicalActivityLevel, userId);
    }

    public void updateUserPreferences(int userId, boolean isVeg, String fitnessGoal, String dietaryPreferences, String meals) throws SQLException {
        int numMealsPerDay = meals.isEmpty() ? 0 : meals.split(",", -1).length;
        String updatePreferencesQuery = "UPDATE UserPreferences SET Num_Meals_Per_Day = ?, isVeg = ?, fitness_goals = ?, Dietary_preference = ?, Preferred_Meal_Timings = ? WHERE User_id = ?";
        tableMapper.prepareAndExecute(updatePreferencesQuery, numMealsPerDay, (isVeg ? 1 : 0), fitnessGoal, dietaryPreferences, meals, userId);
    }

    public void updateUserHealthInfo(int userId, String allergies, String healthConditions) throws SQLException {
        String updateHealthQuery = "UPDATE userDiseases SET allergicPortfolio = ?, metabolicPortfolio = ? WHERE User_id = ?";
        tableMapper.prepareAndExecute(updateHealthQuery, allergies, healthConditions, userId);
    }

    public static void updateUserFromJson(int userId, String jsonData) throws SQLException {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject().getAsJsonObject("user_data");

        String username = jsonObject.get("username").getAsString();
        String email = jsonObject.get("EmailId").getAsString();
        double height = jsonObject.get("height").getAsDouble();
        double weight = jsonObject.get("weight").getAsDouble();
        String activityLevel = jsonObject.get("physical_activity_level").getAsString();
        boolean isVeg = jsonObject.get("isVeg").getAsInt() == 1;
        String fitnessGoal = jsonObject.get("fitness_goals").getAsString();
        String dietaryPreference = jsonObject.get("Dietary_preference").getAsString();
        String mealTimings = concatJsonArray(jsonObject.get("Preferred_Meal_Timings").getAsJsonArray());
        String allergies = concatJsonArray(jsonObject.get("allergicPortfolio").getAsJsonArray());
        String metabolicConditions = concatJsonArray(jsonObject.get("metabolicPortfolio").getAsJsonArray());
        String[] favouriteFoods = jsonArrayToStringArray(jsonObject.get("FavouriteFoods").getAsJsonArray());
        String[] unfavouriteFoods = jsonArrayToStringArray(jsonObject.get("UnfavouriteFoods").getAsJsonArray());

        UserUpdater userUpdater = new UserUpdater();

        userUpdater.updateUser(userId, username, email, height, weight, activityLevel);
        userUpdater.updateUserPreferences(userId, isVeg, fitnessGoal, dietaryPreference, mealTimings);
        userUpdater.updateUserHealthInfo(userId, allergies, metabolicConditions);
        userUpdater.updateUserFavourites(userId, favouriteFoods, unfavouriteFoods);
    }

    private void updateUserFavourites(int userId, String[] favouriteFoods, String[] unfavouriteFoods) {
        String query = "INSERT INTO userFavourites VALUES (?,?,?)";
        for (String food : favouriteFoods) {
            try {
                tableMapper.prepareAndExecute(query, userId, "FAV", food);
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    e.printStackTrace();
                }
            }
        }
        for (String food : unfavouriteFoods) {
            try {
                tableMapper.prepareAndExecute(query, userId, "UNFAV", food);
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String concatJsonArray(JsonArray array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (i > 0) sb.append(","); // Add comma before next element
            sb.append(array.get(i).getAsString());
        }
        return sb.toString();
    }

    private static String[] jsonArrayToStringArray(JsonArray array) {
        String[] result = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = array.get(i).getAsString();
        }
        return result;
    }

}
