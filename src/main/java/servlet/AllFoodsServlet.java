package servlet;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/allFoods")
public class AllFoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String json = "{\"all_foods\":[]}";
        try {
            ArrayList<String> foods = (ArrayList<String>) ServletUtil.dbColumnWiseReader.readColumns("SELECT AFoodName FROM all_foods ORDER BY AFoodName;").get(0);
            json = ServletUtil.gson.toJson(new AllFoods(new ArrayList<>(foods.subList(1, foods.size()))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
        resp.getWriter().close();
    }
    static class AllFoods{
        ArrayList<String> all_foods;
        AllFoods(ArrayList<String> all_foods){
            this.all_foods = all_foods;
        }
    }
}