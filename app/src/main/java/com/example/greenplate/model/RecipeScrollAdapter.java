package com.example.greenplate.model;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.views.RecipeView;
import com.example.greenplate.views.ShoppingListView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeScrollAdapter extends RecyclerView.Adapter<RecipeScrollAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<RecipeModel> list;
    private OnRecipeClickListener onRecipeClickListener;

    private DatabaseReference root;

    private FirebaseDatabase db;

    private User user = User.getInstance();

    public RecipeScrollAdapter(Context context, ArrayList<RecipeModel> list,
                               OnRecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.onRecipeClickListener = listener;
    }
    //code for implementing scrolling mechanism in the Recipe screen
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = FirebaseDatabase.getInstance();
        root = db.getReference().child("ShoppingList");
        View v = LayoutInflater.from(context).inflate(R.layout.recipeitem,
                parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RecipeModel recipeModel = list.get(position);
        holder.recipeName.setText(recipeModel.getRecipeName());
        holder.addToShoppingList.setVisibility(recipeModel.getHasEnoughIngredients() ? View.GONE: View.VISIBLE);
        holder.recipeName.setOnClickListener(view -> {
            if (recipeModel.getHasEnoughIngredients()) {
                onRecipeClickListener.onRecipeClick(recipeModel);
            } else {
                Toast.makeText(context,
                        "You don't have enough ingredients to view recipe details",
                        Toast.LENGTH_SHORT).show();
            }
        });
        Map<String, String> ingredients = recipeModel.getIngredients();
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : ingredients.entrySet()) {
            ingredientsBuilder.append(entry.getKey()).append(": ")
                    .append(entry.getValue()).append("\n");
        }
        holder.ingredients.setText(ingredientsBuilder.toString());
        if (recipeModel.getHasEnoughIngredients()) {
            holder.ingredientCheckmark.setVisibility(View.VISIBLE);
            holder.ingredientCross.setVisibility(View.GONE);
            holder.recipeName.setTextColor(Color.GREEN);
        } else {
            holder.recipeName.setTextColor(Color.RED);
            holder.ingredientCross.setVisibility(View.VISIBLE);
            holder.ingredientCheckmark.setVisibility(View.GONE);

        }

        holder.addToShoppingList.setOnClickListener(view -> {
            //add functionality to iterate through ingredients here
            ArrayList<IngredientsModel> missingIngredients = recipeModel.getMissingIngredients();
            Log.d("onBindViewHolder", "BUTTON CLICKED!");

            for (IngredientsModel ingredient: missingIngredients) {
                Log.d("onBindViewHolder", "INGREDIENT: "
                        + ingredient.getIngredientName() + "QTY: " + ingredient.getQuantity());
                addIngredientToPantry(ingredient.getIngredientName(), ingredient.getQuantity(),
                        ingredient.getCalories(), ingredient.getExpirationDate());
                /*
                1. addIngredientToPantry
                2. duplicate ingredients with different recipes
                addIngredientToPantry(ingredient.getIngredientName(),
                        ingredient.getQuantity(), ingredient.getExpirationDate(),
                        ingredient.getExpirationDate());

                 */
            }

        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeName;
        private TextView ingredients;
        private TextView ingredientCheckmark;
        private TextView ingredientCross;

        private Button addToShoppingList;

        //viewholder for recipiescroll adapter
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.textrecipe);
            ingredients = itemView.findViewById(R.id.textingredients);
            ingredientCheckmark = itemView.findViewById(R.id.ingredientCheckmark);
            ingredientCross = itemView.findViewById(R.id.ingredientCross);
            addToShoppingList = itemView.findViewById(R.id.addToShoppingList);
        }
    }
    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeModel recipeModel);
    }

    public void addIngredientToPantry(String ingredientName, String quantity, String calories,
                                      String expirationDate) {
        // Retrieve the username (email) from the User singleton instance
        String username = user.getUsername();
        if (username != null && !username.isEmpty()) {
            // Use only the part before the '@' symbol in the email as the key
            // and remove any periods or other illegal characters
            String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]",
                    "");

            // Use the sanitized username to create a reference in your database
            DatabaseReference userRef = root.child(sanitizedUsername);
            DatabaseReference ingredientRef = userRef.child(ingredientName);

            Map<String, Object> ingredientData = new HashMap<>();
            ingredientData.put("quantity", quantity);
            ingredientData.put("calories", calories);
            ingredientData.put("expirationDate", expirationDate);

            ingredientRef.setValue(ingredientData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("addIngredientToPantry", "INGREDIENT ADDED!");
                            /*
                            Toast.makeText(RecipeView.this,
                                    "Ingredient added to Shopping List.",
                                    Toast.LENGTH_SHORT).show();
                             */
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("addIngredientToPantry", "INGREDIENT NOT ADDED!: " + e.getMessage());
                            /*
                            Toast.makeText(ShoppingListView.this,
                                    "Failed to add ingredient to shopping list: "
                                            + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                             */
                        }
                    });
        } else {
            Log.d("addIngredientToPantry", "USER AUTH FAILED! ");
            /*
            Toast.makeText(ShoppingListView.this,
                    "Username not set", Toast.LENGTH_SHORT).show();
             */
        }
    }
}
