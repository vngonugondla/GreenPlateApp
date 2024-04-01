package com.example.greenplate.model;

public class IngredientsModel {

    private String ingredientName;
    private String quantity;
    private String calories;
    private String expirationDate;

    public IngredientsModel() {
        this.ingredientName = "";
        this.quantity = "";
        this.calories = "";
    }

    public IngredientsModel(String ingredientName, String quantity, String calories) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.calories = calories;
        this.expirationDate = "00/00/0000";
    }

    public IngredientsModel(String ingredientName, String quantity, String calories,
                            String expirationDate) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.calories = calories;
        this.expirationDate = expirationDate;
    }

    //getter and setter methods
    public String getIngredientName() {
        return ingredientName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getCalories() {
        return calories;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
