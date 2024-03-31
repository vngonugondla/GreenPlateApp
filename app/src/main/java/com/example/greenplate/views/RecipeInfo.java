package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RecipeInfo extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView quantitiesTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        Intent intent = getIntent();
        String recipeName = intent.getStringExtra("recipeName");
        String ingredients = intent.getStringExtra("ingredients");
        recipeNameTextView.setText(recipeName);
        ingredientsTextView.setText(ingredients);
        //BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //bottomNavigationView.setOnNavigationItemSelectedListener(this);
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
