package com.example.greenplate.viewmodels;

import com.example.greenplate.model.RecipeModel;

import java.util.ArrayList;
import java.util.Collections;

public class SortAvailability implements RecipeStrategy {
    @Override
    public ArrayList<RecipeModel> execute(ArrayList<RecipeModel> recipes) {
        Collections.sort(recipes, (recipe1, recipe2) -> Boolean.compare(recipe2.getHasEnoughIngredients(), recipe1.getHasEnoughIngredients()));
        return recipes;
    }
}