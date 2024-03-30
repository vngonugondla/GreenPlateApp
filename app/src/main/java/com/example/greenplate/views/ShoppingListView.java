package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShoppingListView extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Button personalInfoButton = findViewById(R.id.personalInfoButton);

        // Set onClick listener for the button
        personalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the personal information screen
                startActivity(new Intent(ShoppingListView.this, PersonalInfoView.class));
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
}
