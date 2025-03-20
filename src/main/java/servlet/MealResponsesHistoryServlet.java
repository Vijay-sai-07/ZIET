package servlet;

import com.google.gson.JsonObject;
import food.FoodGemini;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@WebServlet("/historyOfMeals")
public class MealResponsesHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int days = Integer.parseInt(req.getParameter("days"));
        int mealId = Integer.parseInt(req.getParameter("mealId"));
        int User_id = (Integer) req.getSession().getAttribute("User_id");
//        int User_id = 1;
        String data = "{\"meal_response_history\":[]}";
        String mealWhereClause = "mealID = ";
        if (mealId < 1 || mealId > 5) {
            mealWhereClause = "1 = 1";
        } else {
            mealWhereClause += mealId;
        }
        ArrayList<Map<String, ?>> arrayList = null;
        try {
            if (days > 0) {
                arrayList = ServletUtil.tableMapper.prepareAndReadMap("SELECT mealID, response, DATE(response_date_time)  FROM user_responses WHERE User_id = ? AND response_date_time >= DATE_SUB(NOW(), INTERVAL ? DAY) AND " + mealWhereClause, User_id, days);
            } else {
                arrayList = ServletUtil.tableMapper.prepareAndReadMap("SELECT mealID, response, DATE(response_date_time) FROM user_responses WHERE User_id = ? AND " + mealWhereClause, User_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (arrayList!=null) {
            data = ServletUtil.gson.toJson(new MealResponsesHistory(arrayList));
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(data);
        resp.getWriter().close();
    }

    static class MealResponsesHistory {
        ArrayList<MealResponse> meal_response_history;

        MealResponsesHistory(ArrayList<Map<String, ?>> meal_response_history) {
            this.meal_response_history = new ArrayList<>();
            for (Map<String, ?> map : meal_response_history) {
                MealResponse mealResponse = new MealResponse();
                mealResponse.mealType = FoodGemini.meals[(Integer) map.get("mealID")-1];
                JsonObject response = ServletUtil.gson.fromJson((String) map.get("response"), JsonObject.class);
                StringBuilder foods = new StringBuilder();
                response.getAsJsonArray("meal_plan").forEach(jsonElement -> foods.append(jsonElement.getAsJsonObject().get("food_item")).append(","));
                if (foods.length() > 0) {
                    foods.deleteCharAt(foods.length() - 1);
                }
                mealResponse.foods = foods.toString();
                mealResponse.calories = response.get("total_calories").getAsInt();
                mealResponse.protein = response.get("total_proteins").getAsInt();
                mealResponse.carbs = response.get("total_carbohydrates").getAsInt();
                mealResponse.fats = response.get("total_fats").getAsInt();
                mealResponse.fibres = response.get("total_fibres").getAsInt();
                Object dateObj = map.get("DATE(response_date_time)");
                mealResponse.date = (dateObj instanceof Date) ? (Date) dateObj : null;
                this.meal_response_history.add(mealResponse);
            }
        }
    }

    static class MealResponse {
        String mealType;
        Date date;
        String foods;
        int calories;
        int protein;
        int carbs;
        int fats;
        int fibres;
    }
}
