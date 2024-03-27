package com.example.greenplate.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.greenplate.R;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.model.PersonalInfoModel;
import com.example.greenplate.model.User;
import com.example.greenplate.viewmodels.IngredientsViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class IngredientsView extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    private EditText ingredientNameEditText;
    private EditText quantityEditText;
    private EditText caloriesEditText;
    private EditText expirationDateEditText;
    private Button submitButton;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Pantry");
    private IngredientsViewModel viewModel;
    private User user = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Button personalInfoButton = findViewById(R.id.personalInfoButton);

        viewModel = new ViewModelProvider(this).get(IngredientsViewModel.class);

        ingredientNameEditText = findViewById(R.id.ingredient_name_edit_text);
        quantityEditText = findViewById(R.id.quantity_edit_text);
        caloriesEditText = findViewById(R.id.calories_edit_text);
        expirationDateEditText = findViewById(R.id.expiration_date_edit_text);
        submitButton = findViewById(R.id.submit_button);

        // Set onClick listener for the button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredientName = ingredientNameEditText.getText().toString();
                String quantity = quantityEditText.getText().toString();
                String calories = caloriesEditText.getText().toString();
                String expirationDate = expirationDateEditText.getText().toString();

                // Navigate to the personal information screen
                startActivity(new Intent(IngredientsView.this, IngredientsView.class));

                if (viewModel.checkIngredientExists(ingredientName)) {
                    Toast.makeText(IngredientsView.this,
                            "Ingredient already exists in pantry.", Toast.LENGTH_SHORT).show();
                } else if (Double.parseDouble(quantity) <= 0) {
                    Toast.makeText(IngredientsView.this,
                            "Quantity must be positive.", Toast.LENGTH_SHORT).show();
                } else {
                    addIngredientToPantry(ingredientName, quantity, calories, expirationDate);
                }
            }
        });
    }

    private void addIngredientToPantry(String ingredientName, String quantity, String calories, String expirationDate) {
        // Retrieve the username (email) from the User singleton instance
        String username = user.getUsername();
        if (username != null && !username.isEmpty()) {
            // Use only the part before the '@' symbol in the email as the key
            // and remove any periods or other illegal characters
            String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]", "");

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
                            Toast.makeText(IngredientsView.this,
                                    "Ingredient added to pantry.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(
                                    IngredientsView.this,
                                    InputMealView.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(IngredientsView.this,
                                    "Failed to add ingredient to pantry: "
                                            + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(IngredientsView.this,
                    "Username not set", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Home) {
            startActivity(new Intent(IngredientsView.this, Home.class));
            return true;
        } else if (id == R.id.Recipe) {
            startActivity(new Intent(IngredientsView.this, RecipeView.class));
            return true;
        } else if (id == R.id.InputMeal) {
            startActivity(new Intent(IngredientsView.this, InputMealView.class));
            return true;
        } else if (id == R.id.Ingredients) {
            return true;
        } else if (id == R.id.ShoppingList) {
            startActivity(new Intent(IngredientsView.this, ShoppingListView.class));
            return true;
        }
        return false;
    }
}
