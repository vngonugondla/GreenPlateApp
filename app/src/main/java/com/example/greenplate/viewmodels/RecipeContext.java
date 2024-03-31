package com.example.greenplate.viewmodels;

import com.example.greenplate.model.RecipeModel;

import java.util.ArrayList;

public class RecipeContext {
    private RecipeStrategy strategy;

    public RecipeContext(RecipeStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RecipeStrategy strategy) {
        this.strategy = strategy;
    }

    public ArrayList<RecipeModel> executeStrategy(ArrayList<RecipeModel> recipes) {
        return strategy.execute(recipes);
    }
}
