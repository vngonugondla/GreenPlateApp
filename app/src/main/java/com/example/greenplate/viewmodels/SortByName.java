package com.example.greenplate.viewmodels;
import com.example.greenplate.model.RecipeModel;
import java.util.ArrayList;
import java.util.Collections;
public class SortByName implements RecipeStrategy {
    @Override
    public ArrayList<RecipeModel> execute(ArrayList<RecipeModel> recipes) {
        Collections.sort(recipes, (recipe1, recipe2) -> recipe1.getRecipeName().compareToIgnoreCase(recipe2.getRecipeName()));
        return recipes;
    }
}
