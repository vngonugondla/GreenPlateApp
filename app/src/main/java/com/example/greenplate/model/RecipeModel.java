package com.example.greenplate.model;

public class RecipeModel {
    private int prepTime;
    private IngredientsModel[] ingredients;
    private String dietCategory;


    //implement getters
    public int getPrepTime() {
        return prepTime;
    }
    public IngredientsModel[] getIngredients() {
        return ingredients;
    }
    public String getDietCategory() {
        return dietCategory;
    }

    //implement setters



}
