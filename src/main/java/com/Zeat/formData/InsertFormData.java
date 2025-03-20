package com.Zeat.formData;

import com.auth.ZohoAuth;
import data_reader.TableMapper;
import database_handler.DBConnector;
import food.JsonFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Response
public class InsertFormData {
    TableMapper tableMapper = new TableMapper(DBConnector.publicConnection);

    public void run(int mealId) {
        ZohoAuth auth = null;
        try {
            auth = new ZohoAuth(new File("client_details.config"), new File("Tokens.protected"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String accessToken = auth.getAuthToken();
        String resourceId = "iw3go480cf567de7b4c919373e9a509988c4f";
        URL url = null;
        try {
            url = new URL("https://sheet.zoho.com/api/v2/" + resourceId);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        StringBuilder postData = new StringBuilder();
        postData.append("method").append("=").append("worksheet.records.add").append("&");
        postData.append("worksheet_name").append("=").append("Sheet1").append("&");
        postData.append("header_row").append("=").append("1").append("&");
        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject1.put("Email", "Joe");
//        jsonObject1.put("meal_id", 2);
//        jsonObject1.put("Response", "gfsdryd jgtf");
//        jsonArray.put(jsonObject1);
//        JSONObject jsonObject2 = new JSONObject();
//        jsonObject2.put("Email", "Beth");
//        jsonObject2.put("meal_id", 1);
//        jsonObject2.put("Response", "hyrd yrd r6 s");
//        jsonArray.put(jsonObject2);
        try {
            for (Map<String, ?> row : tableMapper.prepareAndReadMap("SELECT * FROM user_responses WHERE DATE(response_date_time) = CURDATE() AND mealID = ?", mealId)) {
                JSONObject jsonObject = new JSONObject();
//                System.out.println(row);
                jsonObject.put("Email", tableMapper.prepareAndReadMap("SELECT EmailId FROM user WHERE User_id = ?", row.get("User_id")).get(0).get("EmailId"));
                jsonObject.put("meal_id", row.get("mealID"));
                jsonObject.put("Response", JsonFormatter.parseFoodJson((String) row.get("response"),(int) row.get("mealID")));
                jsonArray.put(jsonObject);
//                System.out.println(jsonObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        postData.append("json_data").append("=").append(jsonArray);
        System.out.println(postData);
        try {
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setRequestProperty("Authorization", "Zoho-oauthtoken " + accessToken);
            httpConn.getOutputStream().write(postData.toString().getBytes("UTF-8"));
            InputStreamReader ir = new InputStreamReader(httpConn.getInputStream());
            int inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = ir.read()) != -1) {
                response.append((char) inputLine);
            }
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}