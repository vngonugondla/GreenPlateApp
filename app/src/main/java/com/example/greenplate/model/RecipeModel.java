package com.example.greenplate.model;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.example.greenplate.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;


public class RecipeModel {
    private String recipeName;
    private Map<String, String> ingredients;
    private boolean hasEnoughIngredients;
    private ArrayList<IngredientsModel> ingredientsList;

    public RecipeModel() {

    }
    public RecipeModel(String recipeName, Map<String, String> ingredients,
                       Boolean hasEnoughIngredients, ArrayList <IngredientsModel> ingredientsList) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.hasEnoughIngredients = hasEnoughIngredients;
        this.ingredientsList = ingredientsList;
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
    public ArrayList<IngredientsModel> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngList(ArrayList<IngredientsModel> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }
    public boolean checkIngredientsSufficiency(Map<String, Integer> availableIngredients) {
        for (Map.Entry<String, String> required : this.ingredients.entrySet()) {
            String ingredient = required.getKey();
            int requiredQuantity = Integer.parseInt(required.getValue());
            Integer availableQuantity = availableIngredients.getOrDefault(ingredient, 0);
            if (availableQuantity < requiredQuantity) {
                return false;
            }
        }
        return true;
    }

    public void calculateTotalCalories(DatabaseReference pantryRef, OnCaloriesCalculatedListener listener) {
        final int[] totalCalories = {0};
        final int[] pendingRequests = {ingredients.size()}; // Counter to track pending Firebase requests.

        for (String ingredientName : this.ingredients.keySet()) {
            pantryRef.child(ingredientName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        IngredientsModel ingredient = dataSnapshot.getValue(IngredientsModel.class);
                        int requiredQuantity = Integer.parseInt(ingredients.get(ingredientName));
                        totalCalories[0] += Integer.parseInt(ingredient.getCalories()) * requiredQuantity;
                    }
                    if (--pendingRequests[0] == 0) {
                        listener.onCalculated(totalCalories[0]);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    // listener interface used for the callback when calorie calculation is complete
    public interface OnCaloriesCalculatedListener {
        void onCalculated(int calories);
    }

}
