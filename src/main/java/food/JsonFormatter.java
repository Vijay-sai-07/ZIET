package food;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonFormatter {

    public static String parseFoodJson(String json, int mealId) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<FoodItem>>() {}.getType();
        StringBuilder stringBuilder = new StringBuilder("Here is Your "+FoodGemini.meals[mealId - 1]+ " Plan :");
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        // Fix: Parse "meal_plan" properly
        ArrayList<FoodItem> foodList = gson.fromJson(jsonObject.get("meal_plan"), listType);

        for (FoodItem foodItem : foodList) {
            stringBuilder.append(foodItem).append('\n');
        }

        // Fix: Get "meal_description" as a string
        return stringBuilder.append("Total Calories in Meal : ").append(jsonObject.get("total_calories")).append('\n')
                .append(" (NOTE: ").append(jsonObject.get("meal_description").getAsString()).append(")")
                .toString();
    }

    static class FoodItem {
        String food_item;
        int quantity_value;
        String quantity_type;
        String food_description;

        @Override
        public String toString() {
            return String.format("%s %d%s - %s",
                    food_item, quantity_value, quantity_type, food_description);
        }
    }
}
