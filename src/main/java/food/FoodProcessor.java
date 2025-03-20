package food;

import com.Zeat.formData.InsertFormData;
import data_reader.TableMapper;
import database_handler.DBConnector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class FoodProcessor extends FoodGemini {
    private final InsertFormData insertFormData = new InsertFormData();
    private TableMapper tableMapper = new TableMapper(DBConnector.publicConnection);
    private final String bareUserQuery = "SELECT     u.User_id,     u.age,     u.height,     u.weight,     up.isVeg,     up.fitness_goals,     up.Dietary_preference,";
    private final String[] queries = {
            "COALESCE(mc1.Calories, 0) ",
            "COALESCE(mc2.Calories, 0) ",
            "COALESCE(mc3.Calories, 0) ",
            "COALESCE(mc4.Calories, 0) ",
            "COALESCE(mc5.Calories, 0) "
    };
    private final String[] joinQueries = {
            "LEFT JOIN MealCalories mc1 ON u.User_id = mc1.User_id AND mc1.Meal_Type = 'BreakFast' ",
            "LEFT JOIN MealCalories mc2 ON u.User_id = mc2.User_id AND mc2.Meal_Type = 'Lunch' ",
            "LEFT JOIN MealCalories mc3 ON u.User_id = mc3.User_id AND mc3.Meal_Type = 'Snack' ",
            "LEFT JOIN MealCalories mc4 ON u.User_id = mc4.User_id AND mc4.Meal_Type = 'Dinner' ",
            "LEFT JOIN MealCalories mc5 ON u.User_id = mc5.User_id AND mc5.Meal_Type = 'Midnight_Dinner' "
    };

    public void executeTask(int session) {
        int start;
        int stop;
        if (session == 1) {
            start = 0;
            stop = 1;
        } else if (session == 2) {
            start = 1;
            stop = 4;
        } else {
            start = 4;
            stop = 5;
        }
        ArrayList<Map<String, ?>> users = null;
        for (int i = start; i < stop; i++) {
            try {
                users = tableMapper.readMap(bareUserQuery + queries[i] + "AS Calories_per_meal, " + "COALESCE(NULLIF(GROUP_CONCAT(DISTINCT CASE WHEN uf.Favourite_status = 'FAV' THEN uf.Food END ORDER BY uf.Food SEPARATOR ', '), ''), 'None') AS FavouriteFoods, COALESCE(NULLIF(GROUP_CONCAT(DISTINCT CASE WHEN uf.Favourite_status = 'UNFAV' THEN uf.Food END ORDER BY uf.Food SEPARATOR ', '), ''), 'None') AS UnfavouriteFoods " + "FROM user u " + "LEFT JOIN UserPreferences up ON u.User_id = up.User_id " + joinQueries[i] + "LEFT JOIN userDiseases ud ON u.User_id = ud.User_Id LEFT JOIN userFavourites uf ON u.User_id = uf.User_id WHERE u.Status = 'Active' GROUP BY u.User_id, u.age, u.height, u.weight, up.isVeg, up.fitness_goals, up.Dietary_preference, mc" + (i + 1) + ".Calories");
                System.out.println("USERS");
                System.out.println(users);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            storeMealRecommendationForUser(users, i + 1);
            insertFormData.run(i + 1);
        }
    }
}