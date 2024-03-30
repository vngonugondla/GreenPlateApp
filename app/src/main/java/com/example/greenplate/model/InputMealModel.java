package com.example.greenplate.model;

public class InputMealModel {
    private String mealName;
    private int calories;
    public InputMealModel(String mealName, int calories) {
        this.mealName = mealName;
        this.calories = calories;
    }

    public String getMealName() {
        return mealName;
    }

    public int getCalories() {
        return calories;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }


}
