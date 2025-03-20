//package servlet;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.sql.SQLException;
//
//@WebServlet("/setUserFavouriteFoods")
//public class SetUserFavouriteFoods extends HttpServlet {
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        int User_id = Integer.parseInt((String) req.getSession().getAttribute("User_id"));
////        int User_id = 1;
//        BufferedReader reader = req.getReader();
//        StringBuilder jsonInput = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            jsonInput.append(line);
//        }
//        JsonObject jsonObject = ServletUtil.gson.fromJson(jsonInput.toString(), JsonObject.class);
//        JsonArray favsArray = jsonObject.getAsJsonArray("favs");
//        JsonArray unfavsArray = jsonObject.getAsJsonArray("unfavs");
//        try {
//            for (JsonElement jsonElement : favsArray) {
//                ServletUtil.tableMapper.prepareAndExecute("INSERT INTO userFavourites (User_id, Food, Favourite_status) VALUES (?,?,?)", User_id, jsonElement.getAsString(), "FAV");
//            }
//            for (JsonElement jsonElement : unfavsArray) {
//                ServletUtil.tableMapper.prepareAndExecute("INSERT INTO userFavourites (User_id, Food, Favourite_status) VALUES  (?,?,?)", User_id, jsonElement.getAsString(), "UNFAV");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
