package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.greenplate.model.MealInfo;
import com.example.greenplate.model.User;
import com.example.greenplate.model.RecipeModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.greenplate.model.IngredientsModel;
import java.util.concurrent.atomic.AtomicInteger;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Locale;

public class RecipeInfo extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private DatabaseReference pantryRef;
    private RecipeModel currentRecipe;

    private int totalCalories = 0;

    private Button btnCook;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Meal");
    private TextView quantitiesTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        Intent intent = getIntent();
        //String recipeName = intent.getStringExtra("recipeName");
        String recipeName = getIntent().getStringExtra("recipeName");
        String ingredients = intent.getStringExtra("ingredients");
        recipeNameTextView.setText(recipeName);
        ingredientsTextView.setText(ingredients);
        //BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //bottomNavigationView.setOnNavigationItemSelectedListener(this);

        calculateTotalCalories(recipeName);

        btnCook = findViewById(R.id.btnCook);
        btnCook.setEnabled(false); // Disable until calorie calculation is done
        btnCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cookRecipe();
            }
        });
    }

    private void calculateTotalCalories(String recipeName) {
        String username = User.getInstance().getUsername().split("@")[0].replaceAll("[.#$\\[\\]]", "");

        DatabaseReference cookbookRef = FirebaseDatabase.getInstance().getReference("Cookbook").child(username).child(recipeName);
        DatabaseReference pantryRef = FirebaseDatabase.getInstance().getReference("Pantry").child(username);

        cookbookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot recipeSnapshot) {
                if (recipeSnapshot.exists()) {
                    AtomicInteger pendingRequests = new AtomicInteger((int) recipeSnapshot.getChildrenCount());

                    for (DataSnapshot ingredientSnapshot : recipeSnapshot.getChildren()) {
                        String ingredientName = ingredientSnapshot.getKey();
                        int requiredQuantity = Integer.parseInt(ingredientSnapshot.getValue(String.class));

                        pantryRef.child(ingredientName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot pantrySnapshot) {
                                if (pantrySnapshot.exists()) {
                                    IngredientsModel pantryIngredient = pantrySnapshot.getValue(IngredientsModel.class);
                                    if (pantryIngredient != null) {
                                        int caloriesPerUnit = Integer.parseInt(pantryIngredient.getCalories());
                                        totalCalories += caloriesPerUnit * requiredQuantity;
                                    }
                                }
                                if (pendingRequests.decrementAndGet() == 0) {
                                    runOnUiThread(() -> btnCook.setEnabled(true));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle possible errors
                            }
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(RecipeInfo.this, "Recipe not found in the cookbook.", Toast.LENGTH_LONG).show();
                        btnCook.setEnabled(false);
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors properly
                runOnUiThread(() -> {
                    Toast.makeText(RecipeInfo.this, "Error fetching cookbook data: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    btnCook.setEnabled(false);
                });
            }
        });
    }




    private void cookRecipe() {
        String recipeName = getIntent().getStringExtra("recipeName");
        String sanitizedUsername = User.getInstance().getUsername().split("@")[0].replaceAll("[.#$\\[\\]]", "");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());

        DatabaseReference userMealsRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(sanitizedUsername).child("Meals");

        DatabaseReference pantryRef = FirebaseDatabase.getInstance().getReference("Pantry").child(sanitizedUsername);
        DatabaseReference cookbookRef = FirebaseDatabase.getInstance().getReference("Cookbook").child(sanitizedUsername).child(recipeName);

        cookbookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot recipeSnapshot) {
                if (recipeSnapshot.exists()) {
                    AtomicInteger totalCalories = new AtomicInteger(0);
                    AtomicInteger pendingRequests = new AtomicInteger((int) recipeSnapshot.getChildrenCount());

                    for (DataSnapshot ingredientSnapshot : recipeSnapshot.getChildren()) {
                        String ingredientName = ingredientSnapshot.getKey();
                        long requiredQuantity = Long.parseLong(ingredientSnapshot.getValue(String.class)); // Ensure we parse as Long

                        pantryRef.child(ingredientName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot pantrySnapshot) {
                                if (pantrySnapshot.exists()) {
                                    long currentQuantity = Long.parseLong(pantrySnapshot.child("quantity").getValue(String.class)); // Safe parsing
                                    long caloriesPerUnit = Long.parseLong(pantrySnapshot.child("calories").getValue(String.class)); // Safe parsing

                                    totalCalories.addAndGet((int) (caloriesPerUnit * requiredQuantity));

                                    long newQuantity = currentQuantity - requiredQuantity;
                                    if (newQuantity <= 0) {
                                        pantrySnapshot.getRef().removeValue();
                                    } else {
                                        pantrySnapshot.getRef().child("quantity").setValue(Long.toString(newQuantity)); // Store as String if needed
                                    }

                                    if (pendingRequests.decrementAndGet() == 0) {
                                        MealInfo meal = new MealInfo(recipeName, totalCalories.get(), todayDate);
                                        userMealsRef.push().setValue(meal)
                                                .addOnSuccessListener(aVoid -> Toast.makeText(RecipeInfo.this, "Meal cooked and added to your records", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Toast.makeText(RecipeInfo.this, "Failed to record meal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(RecipeInfo.this, "Failed to update pantry: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(RecipeInfo.this, "Recipe not found in the cookbook.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RecipeInfo.this, "Error fetching cookbook data: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }






    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Home) {
            startActivity(new Intent(RecipeInfo.this, Home.class));
            return true;
        } else if (id == R.id.Recipe) {
            startActivity(new Intent(RecipeInfo.this, RecipeView.class));
            return true;
        } else if (id == R.id.InputMeal) {
            startActivity(new Intent(RecipeInfo.this, InputMealView.class));
            return true;
        } else if (id == R.id.Ingredients) {
            startActivity(new Intent(RecipeInfo.this, IngredientsView.class));
            return true;
        } else if (id == R.id.ShoppingList) {
            startActivity(new Intent(RecipeInfo.this, ShoppingListView.class));
            return true;
        }
        return false;
    }
}
