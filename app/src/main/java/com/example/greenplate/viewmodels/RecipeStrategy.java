package com.example.greenplate.viewmodels;

import com.example.greenplate.model.RecipeModel;
import java.util.ArrayList;

public interface RecipeStrategy {
    ArrayList<RecipeModel> execute(ArrayList<RecipeModel> recipes);

}
