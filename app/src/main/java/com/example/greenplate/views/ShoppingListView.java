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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.model.ShoppingListModel;
import com.example.greenplate.model.User;
import com.example.greenplate.viewmodels.ShoppingListViewModel;
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
import java.util.List;
import java.util.Map;

public class ShoppingListView extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private EditText ingredientNameEditText;
    private EditText quantityEditText;
    private EditText caloriesEditText;
    private EditText expirationDateEditText;
    private Button submitButton;
    private User user = User.getInstance();

    private FirebaseDatabase db;
    private ShoppingListViewModel viewModel;

    private DatabaseReference root;
    private RecyclerView recyclerView;
    private com.example.greenplate.views.ShoppingListAdapter adapter;
    private ArrayList<ShoppingListModel> shoppingItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        initializeViews();
        setupRecyclerView();
        //fetchIngredients();
        fetchIngredients(new ShoppingListView.IngredientFetchCallback() {
            @Override
            public void onIngredientsFetched(List<ShoppingListModel> ingredients) {
                shoppingItemList.clear();
                shoppingItemList.addAll(ingredients);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ShoppingListView.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchIngredients(ShoppingListView.IngredientFetchCallback callback) {
        String username = User.getInstance().getUsername();
        if (username != null && !username.isEmpty()) {
            String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]", "");
            DatabaseReference userRef = root.child(sanitizedUsername);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<ShoppingListModel> fetchedIngredients = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //IngredientsModel ingredient = snapshot.getValue(IngredientsModel.class);
                        String ingredientStr = snapshot.getKey();
                        String quantityStr;
                        Object quantityObj = snapshot.child("quantity").getValue();
                        if (quantityObj instanceof Long || quantityObj instanceof Integer) {
                            quantityStr = String.valueOf(quantityObj);
                        } else if (quantityObj instanceof String) {
                            // Here, you can directly use the string or attempt to parse it as
                            // a long if necessary
                            quantityStr = (String) quantityObj;
                            try {
                                long quantityLong = Long.parseLong(quantityStr);
                                // If parsing is successful but you still need it as a string
                                quantityStr = String.valueOf(quantityLong);
                            } catch (NumberFormatException e) {
                                // Handle the case where the string cannot be parsed to a long
                                quantityStr = "Invalid Format"; // Adjust based on how you want
                                // to handle this
                            }
                        } else {
                            quantityStr = "Unknown Quantity"; // Adjust accordingly
                        }

                        String caloriesStr = snapshot.child("calories").getValue(String.class);
                        String expirationDateStr = snapshot.child("expirationDate")
                                .getValue(String.class);
                        ShoppingListModel ingredient = new ShoppingListModel(ingredientStr,
                               quantityStr);
                        if (ingredient != null) {
                            fetchedIngredients.add(ingredient);
                        }
                    }
                    callback.onIngredientsFetched(fetchedIngredients);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onError("Failed to load ingredients.");
                }
            });
        } else {
            callback.onError("Username not set or invalid.");
        }
    }

    private void initializeViews() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        ingredientNameEditText = findViewById(R.id.ingredient_name_edit_text);
        quantityEditText = findViewById(R.id.quantity_edit_text);
        caloriesEditText = findViewById(R.id.calories_edit_text);
        expirationDateEditText = findViewById(R.id.expiration_date_edit_text);
        submitButton = findViewById(R.id.submit_button);

        db = FirebaseDatabase.getInstance();
        root = db.getReference().child("ShoppingList");
        viewModel = new ViewModelProvider(this).get(ShoppingListViewModel.class);

        submitButton.setOnClickListener(v -> handleSubmitButtonClick());
        Button scrollUpButton = findViewById(R.id.scrollUpButton1);
        Button scrollDownButton = findViewById(R.id.scrollDownButton1);
        scrollUpButton.setOnClickListener(v -> recyclerView.scrollBy(0, -150));
        scrollDownButton.setOnClickListener(v -> recyclerView.scrollBy(0, 150));

    }


    private void handleSubmitButtonClick() {
        String ingredientName = ingredientNameEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String calories = caloriesEditText.getText().toString().trim();
        String expirationDate = expirationDateEditText.getText().toString().trim();

        if (ingredientName.isEmpty() || quantity.isEmpty() || calories.isEmpty()) {
            Toast.makeText(ShoppingListView.this, "Please fill in all fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double quantityValue = Double.parseDouble(quantity);
            if (quantityValue <= 0) {
                Toast.makeText(ShoppingListView.this, "Quantity must be positive.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(ShoppingListView.this, "Invalid quantity entered.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double calorieValue = Double.parseDouble(calories);
            if (calorieValue <= 0) {
                Toast.makeText(ShoppingListView.this, "Calories must be positive.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(ShoppingListView.this, "Invalid calories entered.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.checkIngredientExists(ingredientName, exists -> {
            if (exists) {
                updateQuantityShoppingList(ingredientName, quantity);
            } else {
                addIngredientToPantry(ingredientName, quantity, calories, expirationDate);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Home) {
            startActivity(new Intent(ShoppingListView.this, Home.class));
            return true;
        } else if (id == R.id.Recipe) {
            startActivity(new Intent(ShoppingListView.this, RecipeView.class));
            return true;
        } else if (id == R.id.InputMeal) {
            startActivity(new Intent(ShoppingListView.this, InputMealView.class));
            return true;
        } else if (id == R.id.Ingredients) {
            startActivity(new Intent(ShoppingListView.this, IngredientsView.class));
            return true;
        } else if (id == R.id.ShoppingList) {
            return true;
        }
        return false;
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
                            Toast.makeText(ShoppingListView.this,
                                    "Ingredient added to Shopping List.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ShoppingListView.this,
                                    "Failed to add ingredient to shopping list: "
                                            + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ShoppingListView.this,
                    "Username not set", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuantityShoppingList(String ingredientName, String quantityToAdd) {
        // Parse quantityToAdd to an integer
        int quantityToAddInt;
        try {
            quantityToAddInt = Integer.parseInt(quantityToAdd);
        } catch (NumberFormatException e) {
            // Handle the case where quantityToAdd is not a valid integer
            Toast.makeText(ShoppingListView.this,
                    "Invalid quantity format.", Toast.LENGTH_SHORT).show();
            return;
        }

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

            // Retrieve the current quantity from the database
            ingredientRef.child("quantity").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Update the quantity by adding the new quantity to the existing one
                        int existingQuantity = snapshot.getValue(Integer.class);
                        int totalQuantity = existingQuantity + quantityToAddInt;

                        // Update the quantity in the database
                        ingredientRef.child("quantity").setValue(totalQuantity)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ShoppingListView.this,
                                                "Quantity updated in Shopping List.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ShoppingListView.this,
                                                "Failed to update quantity in shopping list: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Handle the case where the quantity field doesn't exist
                        Toast.makeText(ShoppingListView.this,
                                "Quantity field doesn't exist for the ingredient.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ShoppingListView.this,
                            "Failed to retrieve quantity from the database: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ShoppingListView.this,
                    "Username not set", Toast.LENGTH_SHORT).show();
        }
    }



    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.shopping_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new com.example.greenplate.views.ShoppingListAdapter(shoppingItemList, root);
        recyclerView.setAdapter(adapter);
    }

    public interface IngredientFetchCallback {
        void onIngredientsFetched(List<ShoppingListModel> ingredients);
        void onError(String message);
    }
}

