package com.example.greenplate.model;
import java.util.Map;

public class RecipeModel {
    private String recipeName;
    private Map<String, String> ingredients;
    private boolean hasEnoughIngredients;

    public RecipeModel() {

    }
    public RecipeModel(String recipeName, Map<String, String> ingredients, Boolean hasEnoughIngredients) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.hasEnoughIngredients = hasEnoughIngredients;
    }
    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Map<String, String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<String, String> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean getHasEnoughIngredients() {
        return hasEnoughIngredients;
    }
    public void setHasEnoughIngredients(boolean hasEnoughIngredients) {
        this.hasEnoughIngredients = hasEnoughIngredients;
    }
}
