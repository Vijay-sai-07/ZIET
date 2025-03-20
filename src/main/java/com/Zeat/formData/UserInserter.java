package com.Zeat.formData;

import data_reader.TableMapper;
import database_handler.DBConnector;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class UserInserter {
    private final TableMapper tableMapper = new TableMapper(DBConnector.publicConnection);

    public void deleteAllUserData() {
        try {
            tableMapper.execute("SET FOREIGN_KEY_CHECKS = 0; " + "TRUNCATE TABLE userFavourites; " + "TRUNCATE TABLE userDiseases; " + "TRUNCATE TABLE user_responses; " + "TRUNCATE TABLE UserPreferences; " + "TRUNCATE TABLE MealCalories; " + "TRUNCATE TABLE user; " + "SET FOREIGN_KEY_CHECKS = 1;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(String username, String chatID, String mail, double height, double weight, String gender, int age, String physicalActivityLevel, boolean eggs, String fitnessGoal, String meals, String dietaryPreferences, String healthConditions, String allergies) throws SQLException {
        System.out.println(chatID);

        String userTableQuery = "INSERT INTO user (username, chat_id, EmailId, height, weight, gender, age, physical_activity_level, water_intake) VALUES (?,?,?,?,?,?,?,?,?)";
        tableMapper.prepareAndExecute(userTableQuery, username, chatID, mail, height, weight, gender, age, physicalActivityLevel, calculateWaterIntake(age, weight, gender.equalsIgnoreCase("MALE"), physicalActivityLevel, fitnessGoal, dietaryPreferences));
        String User_idRetrieval = "SELECT User_id, calories_per_day FROM user WHERE EmailId = ?";
        Map<String, ?> userDetails = tableMapper.prepareAndReadMap(User_idRetrieval, mail).get(0);
        int User_id = (int) userDetails.get("User_id");

        int Num_Meals_Per_Day = meals.isEmpty() ? 0 : meals.split(",", -1).length;
        String UserPreferencesTableQuery = "INSERT INTO UserPreferences (User_id, Num_Meals_Per_Day, isVeg, fitness_goals, Dietary_preference, Preferred_Meal_Timings) VALUES (?,?,?,?,?,?)";
        tableMapper.prepareAndExecute(UserPreferencesTableQuery, User_id, Num_Meals_Per_Day, (eggs) ? 0 : 1, fitnessGoal, dietaryPreferences, meals);

        String userDiseasesTableQuery = "INSERT INTO userDiseases (User_id, allergicPortfolio, metabolicPortfolio) VALUES (?,?,?)";
        tableMapper.prepareAndExecute(userDiseasesTableQuery, User_id, allergies, healthConditions);

        double totalCalories = ((BigDecimal) userDetails.get("calories_per_day")).doubleValue();

        double breakfastRatio = 0.35;
        double lunchRatio = 0.30;
        double dinnerRatio = 0.25;
        double snackRatio = meals.contains("Snacks") ? 0.10 : 0.0; // If user includes Snacks

        String mealCaloriesQuery = "INSERT INTO MealCalories (User_id, Meal_Type, Calories) VALUES (?,?,?)";

        if (meals.contains("BreakFast")) {
            tableMapper.prepareAndExecute(mealCaloriesQuery, User_id, "BreakFast", Math.round(totalCalories * breakfastRatio));
        }
        if (meals.contains("Lunch")) {
            tableMapper.prepareAndExecute(mealCaloriesQuery, User_id, "Lunch", Math.round(totalCalories * lunchRatio));
        }
        if (meals.contains("Dinner")) {
            tableMapper.prepareAndExecute(mealCaloriesQuery, User_id, "Dinner", Math.round(totalCalories * dinnerRatio));
        }
        if (meals.contains("Snacks")) {
            tableMapper.prepareAndExecute(mealCaloriesQuery, User_id, "Snacks", Math.round(totalCalories * snackRatio));
        }
        if (meals.contains("Midnight_Dinner")) {
            tableMapper.prepareAndExecute(mealCaloriesQuery, User_id, "Midnight_Dinner", Math.round(totalCalories * 0.05));
        }
    }

    private double calculateWaterIntake(int age, double weight, boolean isMale,
                                        String activityLevel, String fitnessGoal, String dietaryPreference) {
        double baseWaterIntake = (age < 18) ? weight * 40 : weight * 35; // Base intake formula

        // Adjust for activity level
        switch (activityLevel.toUpperCase()) {
            case "MODERATE":
                baseWaterIntake += 500;
                break;
            case "HIGH":
                baseWaterIntake += 1000;
                break;
            case "LOW":
                break;
        }

        // Adjust for fitness goals
        switch (fitnessGoal.toUpperCase()) {
            case "WEIGHT_GAIN":
                baseWaterIntake += 500;
                break;
            case "WEIGHT_LOSS":
                baseWaterIntake += 750;
                break;
            case "MAINTAIN_WEIGHT":
                break;
        }

        // Adjust for dietary preference
        switch (dietaryPreference.toUpperCase()) {
            case "PROTEIN_RICH":
                baseWaterIntake += 300;
        }

        // Gender-based adjustment (Males need ~10% more water)
        if (isMale) {
            baseWaterIntake *= 1.1;
        }

        return baseWaterIntake; // in milliliters
    }

    private static String encrypt(String message) {
        MessageDigest messageDigest;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(message.getBytes());
            System.out.println(Arrays.toString(hashBytes));
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    stringBuilder.append('0');
                }
                stringBuilder.append(hex);
            }

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Unable to find encryption algorithm : " + e.getMessage());
        }
        return stringBuilder.toString();
    }
}
