package com.Zeat.formData;

public class Users {
	private String username;
    private String chatID;
    private String mail;
    private double height;
    private double weight;
    private String gender;
    private int age;
    private String physicalActivityLevel;
    private boolean eggs;
    private String fitnessGoal;
    private String meals;
    private String dietaryPreferences;
    private String healthConditions;
    private String allergies;

    // Getters and Setters
    public String getUsername() {
    	return username;
    }
    
    public String getChatID() {
        return chatID;
    }

    public void setUsername(String Username) {
        this.username = Username;
    }
    
    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhysicalActivityLevel() {
        return physicalActivityLevel;
    }

    public void setPhysicalActivityLevel(String physicalActivityLevel) {
        this.physicalActivityLevel = physicalActivityLevel;
    }

    public boolean isEggs() {
        return eggs;
    }

    public void setEggs(boolean eggs) {
        this.eggs = eggs;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public String getMeals() {
        return meals;
    }

    public void setMeals(String meals) {
        this.meals = meals;
    }

    public String getDietaryPreferences() {
        return dietaryPreferences;
    }

    public void setDietaryPreferences(String dietaryPreferences) {
        this.dietaryPreferences = dietaryPreferences;
    }

    public String getHealthConditions() {
        return healthConditions;
    }

    public void setHealthConditions(String healthConditions) {
        this.healthConditions = healthConditions;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

}