package com.example.greenplate.model;

public class MealInfo {
    private String mealName;
    private int calories;
    private String date;

    public MealInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(MealInfo.class)
    }

    public MealInfo(String mealName, int calories, String date) {
        this.mealName = mealName;
        this.calories = calories;
        this.date = date;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
