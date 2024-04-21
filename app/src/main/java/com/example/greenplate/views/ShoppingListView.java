package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.greenplate.views.ShoppingListAdapter;
import com.example.greenplate.model.User;
import com.example.greenplate.viewmodels.IngredientsViewModel;
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
    private IngredientsViewModel viewModelIng;

    private DatabaseReference root;
    private RecyclerView recyclerView;
    private ShoppingListAdapter adapter;
    private ArrayList<ShoppingListModel> shoppingItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);

        initializeViews();
        setupRecyclerView();
        //fetchIngredients();

        viewModelIng = new ViewModelProvider(this).get(IngredientsViewModel.class);

        Button buyItemsButton = findViewById(R.id.buyItemsButton);
        buyItemsButton.setOnClickListener(v -> buySelectedItems());
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


    private void buySelectedItems() {
        for (int i = adapter.getItemCount() - 1; i >= 0; i--) {
            com.example.greenplate.views.ShoppingListAdapter.ShoppingListViewHolder holder = (com.example.greenplate.views.ShoppingListAdapter.ShoppingListViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null && holder.isChecked()) {
                String itemName = holder.getItemName();
                String itemQuantity = holder.getQuantity();
                if (viewModelIng != null) {
                    viewModelIng.checkIngredientExists(itemName, exists -> {
                        if (exists) {
                            updatePantryQuantity(itemName, itemQuantity, new IngredientCheckCallback() {
                                @Override
                                public void onCheckCompleted(boolean success) {
                                    if (success) {
                                        Toast.makeText(ShoppingListView.this, itemName + " updated in pantry.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ShoppingListView.this, "Failed to update " + itemName + " in pantry.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            //Toast.makeText(ShoppingListView.this,
                             //       "Ingredient already exists in pantry.", Toast.LENGTH_SHORT).show();
                        } else {
                            //addIngredientToPantry(itemName, itemQuantity, "0", "00/00/0000");
                            addIngredientToPantry2(itemName, itemQuantity, "0", "00/00/0000", new PantryUpdateCallback() {
                                @Override
                                public void onUpdateCompleted(boolean success) {
                                    if (success) {
                                        // Handle successful addition
                                        // Maybe refresh the list or update UI
                                    } else {
                                        // Handle failure
                                        // Show error message or log the issue
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(this, "ViewModel is not initialized", Toast.LENGTH_SHORT).show();
                }
                //removeFromShoppingList(itemName);  // Implement this method
                handleShoppingListRemoval(itemName, i);
                //addToPantry(itemName, itemQuantity);// Optionally add to pantry here or update any other state
                shoppingItemList.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
    }

   /* private void removeFromShoppingList(String itemName) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("ShoppingList").child(stripUsername());
        userRef.child(itemName).removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(ShoppingListView.this, itemName + " purchased and removed from list.", Toast.LENGTH_SHORT).show();
        });
    }*/
   private void handleShoppingListRemoval(String itemName, int position) {
       removeFromShoppingList(itemName, success -> {
           if (success) {
               // Synchronize access to shoppingItemList if it might be accessed from multiple threads
               synchronized (shoppingItemList) {
                   // Check if the position is still valid
                   if (position < shoppingItemList.size()) {
                       // Remove the item from the local data model as well
                       shoppingItemList.remove(position);
                       adapter.notifyItemRemoved(position);
                       runOnUiThread(() -> Toast.makeText(ShoppingListView.this, itemName + " successfully removed.", Toast.LENGTH_SHORT).show());
                   }
               }
           } else {
               runOnUiThread(() -> Toast.makeText(ShoppingListView.this, "Error removing " + itemName + " from list.", Toast.LENGTH_SHORT).show());
           }
       });
   }



    public interface ShoppingListUpdateCallback {
        void onCompleted(boolean success);
    }

    private void removeFromShoppingList(String itemName, ShoppingListUpdateCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("ShoppingList").child(stripUsername());
        userRef.child(itemName).removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(ShoppingListView.this, itemName + " purchased and removed from list.", Toast.LENGTH_SHORT).show();
            callback.onCompleted(true);  // Notify callback of success
        }).addOnFailureListener(e -> {
            Toast.makeText(ShoppingListView.this, "Failed to remove " + itemName + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
            callback.onCompleted(false);  // Notify callback of failure
        });
    }


    private void updatePantryQuantity(String ingredientName, String quantityToAdd, IngredientCheckCallback callback) {
        int quantityToAddInt;
        try {
            quantityToAddInt = Integer.parseInt(quantityToAdd);
        } catch (NumberFormatException e) {
            Toast.makeText(ShoppingListView.this, "Invalid quantity format.", Toast.LENGTH_SHORT).show();
            callback.onCheckCompleted(false);  // Notify callback of failure due to format issues
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Pantry").child(stripUsername());
        DatabaseReference ingredientRef = userRef.child(ingredientName);

        ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double currentQuantity = 0.0;  // Default current quantity to zero if not present
                if (snapshot.exists() && snapshot.child("quantity").getValue() != null) {
                    Object quantityObj = snapshot.child("quantity").getValue();
                    if (quantityObj instanceof Long) {
                        currentQuantity = ((Long) quantityObj).doubleValue();
                    } else if (quantityObj instanceof String) {
                        try {
                            currentQuantity = Double.parseDouble((String) quantityObj);
                        } catch (NumberFormatException e) {
                            Toast.makeText(ShoppingListView.this, "Error parsing existing quantity.", Toast.LENGTH_SHORT).show();
                            callback.onCheckCompleted(false);
                            return;
                        }
                    }
                }

                // Calculate the new total quantity
                double newQuantity = currentQuantity + quantityToAddInt;

                // Update the quantity in the database
                ingredientRef.child("quantity").setValue(newQuantity)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ShoppingListView.this, "Pantry updated successfully.", Toast.LENGTH_SHORT).show();
                            callback.onCheckCompleted(true);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ShoppingListView.this, "Failed to update pantry: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            callback.onCheckCompleted(false);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShoppingListView.this, "Failed to access database: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onCheckCompleted(false);
            }
        });
    }


    public interface IngredientCheckCallback {
        void onCheckCompleted(boolean exists);
    }

    public interface PantryUpdateCallback {
        void onUpdateCompleted(boolean success);
    }

    public void addIngredientToPantry2(String ingredientName, String quantity, String calories, String expirationDate, PantryUpdateCallback callback) {
        String username = user.getUsername();
        if (username != null && !username.isEmpty()) {
            String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]", "");
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Pantry").child(stripUsername());
            DatabaseReference ingredientRef = userRef.child(ingredientName);

            Map<String, Object> ingredientData = new HashMap<>();
            ingredientData.put("quantity", quantity);
            ingredientData.put("calories", calories);
            ingredientData.put("expirationDate", expirationDate);

            ingredientRef.setValue(ingredientData)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(ShoppingListView.this, "Ingredient added to Pantry.", Toast.LENGTH_SHORT).show();
                        callback.onUpdateCompleted(true);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ShoppingListView.this, "Failed to add ingredient to pantry: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        callback.onUpdateCompleted(false);
                    });
        } else {
            Toast.makeText(ShoppingListView.this, "Username not set", Toast.LENGTH_SHORT).show();
            callback.onUpdateCompleted(false);  // Notify callback of failure due to missing username
        }
    }




    private String stripUsername() {
        String username = User.getInstance().getUsername();
        if (username != null && !username.isEmpty()) {
            String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]", "");
            //DatabaseReference userRef = root.child(sanitizedUsername);
            return sanitizedUsername;
        }
        return null;
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
                        try {
                            String ingredientName = snapshot.getKey();
                            String quantityStr = convertQuantity(snapshot.child("quantity").getValue());
                            String caloriesStr = snapshot.child("calories").getValue(String.class);
                            String expirationDateStr = snapshot.child("expirationDate").getValue(String.class);

                            ShoppingListModel ingredient = new ShoppingListModel(ingredientName, quantityStr, caloriesStr, expirationDateStr);
                            fetchedIngredients.add(ingredient);
                        } catch (Exception e) {
                            Log.e("ShoppingListView", "Error parsing ingredient data", e);
                            callback.onError("Error parsing data for " + snapshot.getKey());
                            return;
                        }
                    }
                    callback.onIngredientsFetched(fetchedIngredients);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ShoppingListView", "Database error: " + databaseError.getMessage());
                    callback.onError("Failed to load ingredients.");
                }
            });
        } else {
            callback.onError("Username not set or invalid.");
        }
    }

    private String convertQuantity(Object quantityObj) {
        if (quantityObj instanceof Long || quantityObj instanceof Integer) {
            return String.valueOf(quantityObj);
        } else if (quantityObj instanceof String) {
            try {
                // Convert to integer first to remove any decimals
                return String.valueOf(Integer.parseInt((String) quantityObj));
            } catch (NumberFormatException e) {
                Log.e("ShoppingListView", "Failed to parse quantity: " + quantityObj, e);
                return "Invalid Format";
            }
        }
        return "Unknown";
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

