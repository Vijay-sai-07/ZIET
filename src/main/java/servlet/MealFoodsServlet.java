package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/mealFoods")
public class MealFoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int mealId = Integer.parseInt(req.getParameter("mealId"));
//        int mealId = 3;
        String data = "{\"meal_foods\":[]}";
        try {
            ArrayList<String> foods = (ArrayList<String>) ServletUtil.dbColumnWiseReader.prepareAndReadColumns("SELECT FoodName FROM food_items WHERE mealID = ?", mealId).get(0);
            data = ServletUtil.gson.toJson(new MealFoods(new ArrayList<>(foods.subList(0, foods.size()))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(data);
        resp.getWriter().close();
    }
    static class MealFoods{
        ArrayList<String> meal_foods;
        MealFoods(ArrayList<String> foods){
            this.meal_foods = foods;
        }
    }
}
