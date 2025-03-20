package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

//gives user details when mail given as input
@WebServlet("/userDetails")
public class UserDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int User_id = (Integer) req.getSession().getAttribute("User_id");
        String query = "SELECT     u.username,      u.EmailId,      u.height,      u.weight,      u.physical_activity_level, up.isVeg,      up.fitness_goals,      up.Dietary_preference,      up.Preferred_Meal_Timings,    ud.allergicPortfolio, ud.metabolicPortfolio, COALESCE(NULLIF(GROUP_CONCAT(DISTINCT CASE WHEN uf.Favourite_status = 'FAV' THEN uf.Food END ORDER BY uf.Food SEPARATOR ', '), ''), 'None') AS FavouriteFoods,      COALESCE(NULLIF(GROUP_CONCAT(DISTINCT CASE WHEN uf.Favourite_status = 'UNFAV' THEN uf.Food END ORDER BY uf.Food SEPARATOR ', '), ''), 'None') AS UnfavouriteFoods  FROM user u  LEFT JOIN UserPreferences up ON u.User_id = up.User_id  LEFT JOIN userDiseases ud ON u.User_id = ud.User_Id  LEFT JOIN userFavourites uf ON u.User_id = uf.User_id  WHERE u.Status = 'Active'  AND u.User_id = ? GROUP BY u.username, u.age, u.height, u.weight, up.isVeg, up.fitness_goals, up.Dietary_preference, up.Preferred_Meal_Timings";
        String data = "{\"user_data\": {}}";
        try{
            data = ServletUtil.gson.toJson(new UserDetails(ServletUtil.tableMapper.prepareAndReadMap(query,User_id).get(0)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(data);
        resp.getWriter().close();
    }
    static class UserDetails{
        Map<String, ?> user_data;
        UserDetails(Map<String, ?> user_data){
            this.user_data = user_data;
        }
    }
}
