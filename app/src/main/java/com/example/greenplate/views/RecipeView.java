package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.model.RecipeModel;
import com.example.greenplate.model.RecipeScrollAdapter;
import com.example.greenplate.model.User;
import com.example.greenplate.viewmodels.IngredientsViewModel;
import com.example.greenplate.viewmodels.RecipeContext;
import com.example.greenplate.viewmodels.RecipeStrategy;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.example.greenplate.viewmodels.SortAvailability;
import com.example.greenplate.viewmodels.SortByName;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class RecipeView extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        RecipeScrollAdapter.OnRecipeClickListener {

    private EditText ingredientNameEditText;
    private EditText quantityEditText;
    private EditText recipeNameEditText;
    private Button addIngredientButton;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Cookbook");
    private RecipeViewModel viewModel;

    private IngredientsViewModel ingredientsViewModel;
    private RecipeModel model;
    private User user = User.getInstance();
    private RecyclerView recyclerView;
    private RecipeScrollAdapter adapter;
    private ArrayList<RecipeModel> list;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ingredientsViewModel = new ViewModelProvider(this).get(IngredientsViewModel.class);
        recyclerView = findViewById(R.id.recipeScrollList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(true);
        DatabaseReference cookbookRef = FirebaseDatabase.getInstance()
                .getReference().child("Cookbook");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeScrollAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        String userN = user.getUsername().split("@")[0].replaceAll("[.#$\\[\\]]", "");
        DatabaseReference userRef = root.child(userN);
        spinner = findViewById(R.id.sortingSpinner);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String recipeName = dataSnapshot.getKey();
                    Map<String, String> ingredients = (Map<String, String>) dataSnapshot.getValue();
                    list.add(new RecipeModel(recipeName, ingredients, false));
                }
                checkIngredientSufficiency();
                RecipeContext context;
                context = new RecipeContext(new SortAvailability());
                list = context.executeStrategy(list);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeView.this, "Error fetching recipes: "
                        + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Button scrollUpButton = findViewById(R.id.scrollUpButton);
        Button scrollDownButton = findViewById(R.id.scrollDownButton);
        scrollUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollBy(0, -100);
            }
        });

        scrollDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollBy(0, 100);
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Button personalInfoButton = findViewById(R.id.personalInfoButton);
        personalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecipeView.this, PersonalInfoView.class));
            }
        });
        recipeNameEditText = findViewById(R.id.recipeNameEditText);
        ingredientNameEditText = findViewById(R.id.ingredientNameEditText);
        quantityEditText = findViewById(R.id.ingredientQuantityEditText);
        addIngredientButton = findViewById(R.id.addIngredientButton);
        addIngredientButton.setOnClickListener(v -> {
            String recipeName = recipeNameEditText.getText().toString();
            String ingredientName = ingredientNameEditText.getText().toString();
            String quantity = quantityEditText.getText().toString().trim();
            if (recipeName.isEmpty() || ingredientName.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(RecipeView.this,
                        "Please enter both recipe name, ingredient name, and quantity",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Split the quantity string by commas and concatenate the resulting substrings
                String[] quantityParts = quantity.split(",");
                // Check if the quantity is a valid number
                try {
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
        });
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        applySortingStrategy(new SortAvailability());
                        break;
                    case 1:
                        applySortingStrategy(new SortByName());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void applySortingStrategy(RecipeStrategy strategy) {
        if (list != null && !list.isEmpty()) {
            RecipeContext context = new RecipeContext(strategy);
            list = context.executeStrategy(list);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void checkIngredientSufficiency() {
        String username = user.getUsername();
        if (username == null || username.isEmpty()) {
            Toast.makeText(RecipeView.this,
                    "Username is not set. Please log in.", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference pantryRef = FirebaseDatabase.getInstance().getReference()
                .child("Pantry").child(username.split("@")[0]
                        .replaceAll("[.#$\\[\\]]", ""));
        pantryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot pantrySnapshot) {
                if (!pantrySnapshot.exists()) {
                    Toast.makeText(RecipeView.this, "Pantry data not found.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                for (RecipeModel recipe : list) {
                    boolean hasEnough = true;
                    Map<String, String> ingredients = recipe.getIngredients();

                    if (ingredients == null) {
                        hasEnough = false;
                    } else {
                        for (Map.Entry<String, String> entry : ingredients.entrySet()) {
                            String ingredient = entry.getKey();
                            String requiredQuantityStr = entry.getValue();
                            try {
                                int requiredQuantity = Integer.parseInt(requiredQuantityStr);
                                DataSnapshot pantryIngredientSnapshot = pantrySnapshot
                                        .child(ingredient);

                                if (!pantryIngredientSnapshot.exists()) {
                                    hasEnough = false;
                                    break;
                                }
                                Object pantryQuantityObj = pantryIngredientSnapshot
                                        .child("quantity").getValue();
                                int pantryQuantity = 0;
                                if (pantryQuantityObj instanceof Long) {
                                    pantryQuantity = ((Long) pantryQuantityObj).intValue();
                                } else if (pantryQuantityObj instanceof String) {
                                    pantryQuantity = Integer.parseInt((String) pantryQuantityObj);
                                } else {
                                    hasEnough = false;
                                    break;
                                }

                                if (pantryQuantity < requiredQuantity) {
                                    hasEnough = false;
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                hasEnough = false;
                                break;
                            }
                        }
                    }

                    recipe.setHasEnoughIngredients(hasEnough);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeView.this, "Failed to fetch pantry data.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRecipe(String recipeName, String ingredientNameList, String quantityList) {
        String username = user.getUsername();
        if (username != null && !username.isEmpty()) {
            String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]",
                    "");

            DatabaseReference userRef = root.child(sanitizedUsername);

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

            userRef.child(recipeName).setValue(recipeIngredients)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RecipeView.this,
                                    "Recipe added to cookbook.",
                                    Toast.LENGTH_SHORT).show();
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
    public void onRecipeClick(RecipeModel recipeModel) {
        Intent intent = new Intent(this, RecipeInfo.class);
        intent.putExtra("recipeName", recipeModel.getRecipeName());
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : recipeModel.getIngredients().entrySet()) {
            ingredientsBuilder.append(entry.getKey()).append(": ").append(entry.getValue())
                    .append("\n");
        }
        intent.putExtra("ingredients", ingredientsBuilder.toString());
        startActivity(intent);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Home) {
            startActivity(new Intent(RecipeView.this, Home.class));
            return true;
        } else if (id == R.id.Recipe) {
            startActivity(new Intent(RecipeView.this, RecipeView.class));
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
