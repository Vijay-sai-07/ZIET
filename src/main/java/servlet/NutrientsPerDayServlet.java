package servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/nutrientsPerDay")
public class NutrientsPerDayServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int User_id = (Integer) req.getSession().getAttribute("User_id");
//        int User_id = 1;
        ArrayList<Map<String, ?>> data = null;

        try {
            data = ServletUtil.tableMapper.prepareAndReadMap("SELECT mealID, response FROM user_responses WHERE User_id = ? AND DATE(CONVERT_TZ(response_date_time, '+00:00', @@session.time_zone)) = CURDATE()", User_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(data);
        if (data == null) {
            data = new ArrayList<>();
        }

        data = verifyNutrientData(data);
        System.out.println(data);
        String json = ServletUtil.gson.toJson(new NutrientsPerDay(prepareJsonData(data)));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
        resp.getWriter().close();
        System.out.println(json);
    }

    private ArrayList<Map<String, ?>> verifyNutrientData(ArrayList<Map<String, ?>> data) {
        int[] mealsToDelete = {}; // Default: no deletions
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        if (hour > 19) { // After 7 PM, delete meal 5
            mealsToDelete = new int[]{5};
        } else if (hour > 16) { // After 4 PM, delete meal 4 & 5
            mealsToDelete = new int[]{4, 5};
        } else if (hour > 12) { // After 12 PM, delete meal 3, 4, & 5
            mealsToDelete = new int[]{3, 4, 5};
        } else if (hour > 8) { // After 8 AM, delete meal 2, 3, 4, & 5
            mealsToDelete = new int[]{2, 3, 4, 5};
        } // Before 8 AM, all meals are kept

        return deleteMealsFromData(data, mealsToDelete);
    }


    private ArrayList<Map<String, ?>> deleteMealsFromData(ArrayList<Map<String, ?>> data, int[] mealIds) {
        ArrayList<Map<String, ?>> newData = new ArrayList<>();
        for (Map<String, ?> map : data) {
            int mealId = parseMealID(map.get("mealID"));
            if (Arrays.stream(mealIds).noneMatch(i -> i == mealId)) {
                newData.add(map); // Keep meals that are NOT in mealIds
            }
        }
        return newData;
    }


    private int parseMealID(Object mealIDObj) {
        if (mealIDObj instanceof Number) {
            return ((Number) mealIDObj).intValue();
        }
        return Integer.parseInt(mealIDObj.toString());
    }

    private ArrayList<Map<String, Object>> prepareJsonData(ArrayList<Map<String, ?>> data) {
        ArrayList<Map<String, Object>> newData = new ArrayList<>();

        for (Map<String, ?> obj : data) {
            HashMap<String, Object> newObj = new HashMap<>();
            newObj.put("mealID", parseMealID(obj.get("mealID")));
            JsonObject jsonObject = JsonParser.parseString(obj.get("response").toString()).getAsJsonObject();
            newObj.put("meal_calories", jsonObject.get("total_calories").getAsFloat());
            newObj.put("meal_carbohydrates", jsonObject.get("total_carbohydrates").getAsFloat());
            newObj.put("meal_proteins", jsonObject.get("total_proteins").getAsFloat());
            newObj.put("meal_fats", jsonObject.get("total_fats").getAsFloat());
            newObj.put("meal_fibres", jsonObject.get("total_fibres").getAsFloat());

            newData.add(newObj);
        }
        return newData;
    }

    static class NutrientsPerDay {
        ArrayList<Map<String, Object>> nutrients;

        NutrientsPerDay(ArrayList<Map<String, Object>> nutrients) {
            this.nutrients = nutrients;
        }
    }
}
