package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.model.User;
import com.example.greenplate.viewmodels.IngredientsViewModel;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RecipeView extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private EditText ingredientNameEditText;
    private EditText quantityEditText;
    private EditText recipeNameEditText;
    private Button addIngredientButton;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Cookbook");
    private RecipeViewModel viewModel;
    private User user = User.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Button personalInfoButton = findViewById(R.id.personalInfoButton);
        personalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecipeView.this, PersonalInfoView.class));
            }
        });

        viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        recipeNameEditText = findViewById(R.id.recipeNameEditText);
        ingredientNameEditText = findViewById(R.id.ingredientNameEditText);
        quantityEditText = findViewById(R.id.ingredientQuantityEditText);
        addIngredientButton = findViewById(R.id.addIngredientButton);

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipeName = recipeNameEditText.getText().toString();
                String ingredientName = ingredientNameEditText.getText().toString();
                String quantity = quantityEditText.getText().toString().trim(); // Trim any leading or trailing spaces

                if (recipeName.isEmpty() || ingredientName.isEmpty() || quantity.isEmpty()) {
                    Toast.makeText(RecipeView.this,
                            "Please enter both recipe name, ingredient name, and quantity",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Split the quantity string by commas and concatenate the resulting substrings
                    String[] quantityParts = quantity.split(",");
                    //StringBuilder quantityBuilder = new StringBuilder();
                    //for (String part : quantityParts) {
                    //    quantityBuilder.append(part.trim()); // Trim any leading or trailing spaces in each part
                    //}
                    //quantity = quantityBuilder.toString();

                    // Check if the quantity is a valid number
                    try {
                        //double quantityValue = Double.parseDouble(quantity);
                        for (String part: quantityParts) {
                            double quantityValue = Double.parseDouble(part);
                            if (quantityValue <= 0) {
                                Toast.makeText(RecipeView.this,
                                        "Quantity must be positive",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(RecipeView.this,
                                "Invalid quantity format",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addRecipe(recipeName, ingredientName, quantity);
                }
            }
        });
    }

    private void addRecipe(String recipeName, String ingredientNameList, String quantityList) {
        // Retrieve the username (email) from the User singleton instance
        String username = user.getUsername();
        if (username != null && !username.isEmpty()) {
            // Use only the part before the '@' symbol in the email as the key
            // and remove any periods or other illegal characters
            String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]", "");

            // Use the sanitized username to create a reference in your database
            DatabaseReference userRef = root.child(sanitizedUsername);

            // Create a map to store ingredients and their quantities
            Map<String, String> recipeIngredients = new HashMap<>();

            // Split the ingredientNameList string by commas to get individual ingredients
            String[] ingredients = ingredientNameList.split(",");
            // Split the quantityList string by commas to get individual quantities
            String[] quantities = quantityList.split(",");

            // Check if the number of ingredients matches the number of quantities
            if (ingredients.length != quantities.length) {
                Toast.makeText(RecipeView.this,
                        "Number of ingredients must match number of quantities",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Loop through each ingredient and quantity pair
            for (int i = 0; i < ingredients.length; i++) {
                String ingredientName = ingredients[i].trim();
                String quantity = quantities[i].trim();

                // Check if the quantity is a valid number
                try {
                    double quantityValue = Double.parseDouble(quantity);
                    if (quantityValue <= 0) {
                        Toast.makeText(RecipeView.this,
                                "Quantity for " + ingredientName + " must be positive",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(RecipeView.this,
                            "Invalid quantity format for " + ingredientName,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add the ingredient with its quantity to the recipeIngredients map
                recipeIngredients.put(ingredientName, quantity);
            }

            // Add the recipe name and its ingredients to the user's reference
            userRef.child(recipeName).setValue(recipeIngredients)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RecipeView.this,
                                    "Recipe added to cookbook.",
                                    Toast.LENGTH_SHORT).show();

                            // Navigate to the desired activity after successful addition
                            Intent intent = new Intent(RecipeView.this, InputMealView.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RecipeView.this,
                                    "Failed to add recipe: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(RecipeView.this,
                    "Username not set", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Home) {
            startActivity(new Intent(RecipeView.this, Home.class));
            return true;
        } else if (id == R.id.Recipe) {
            return true;
        } else if (id == R.id.InputMeal) {
            startActivity(new Intent(RecipeView.this, InputMealView.class));
            return true;
        } else if (id == R.id.Ingredients) {
            startActivity(new Intent(RecipeView.this, IngredientsView.class));
            return true;
        } else if (id == R.id.ShoppingList) {
            startActivity(new Intent(RecipeView.this, ShoppingListView.class));
            return true;
        }
        return false;
    }
}
