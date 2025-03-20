package com.Zeat.api;

import data_reader.DBRowWiseReader;
import database_handler.DBConnector;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBInsertions {
    private static final DBRowWiseReader dbRowWiseReader = new DBRowWiseReader(DBConnector.publicConnection);

//	public DBInsertions(String session,String FoodName,String Quantity_type,BigDecimal Quantity_value,int Calories,String contains){
//		insertData(session,FoodName,Quantity_type,Quantity_value,Calories,contains);
//	}
    private final List<String> foods;

    public DBInsertions(List<String> foods){
        this.foods = foods;
    }
    public static void deleteData() {
        String deleteQuery="TRUNCATE TABLE food_items";
        try {
            dbRowWiseReader.execute(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public void insertData(String session,String FoodName,String Quantity_type,BigDecimal Quantity_value,int Calories,String contains) {
        String insertQuery = "INSERT INTO food_items(Sid, mealID, FoodName, Quantity_type, Quantity_value, Calories,Contains) VALUES (?,?,?,?,?,?,?)"; // Modify the table and column names
    	String insertInto = "INSERT INTO all_foods (AFoodName) VALUES (?)"; //MODIFY IN ALL FOODS TABLE
        String fetch="select Sid,mealID from session where session_name=?";
        try {
            ArrayList<Integer> arrayList = (ArrayList<Integer>) dbRowWiseReader.prepareAndReadRowsWithoutColumnNames(fetch,session).get(0);
            dbRowWiseReader.prepareAndExecute(insertQuery, arrayList.get(0),arrayList.get(1),FoodName,Quantity_type,Quantity_value,Calories,contains);
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        try {
            if (!foodExists(FoodName)) {
                dbRowWiseReader.prepareAndExecute(insertInto, FoodName);
            }
        } catch (SQLException e){
           
        }
    }

    private boolean foodExists(String foodName){
        for (String food: foods){
            if (foodName.equalsIgnoreCase(food)) {
                return true;
            }
        }
        return false;
    }
}