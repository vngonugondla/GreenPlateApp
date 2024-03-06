package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.model.InputMealModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class InputMealView extends AppCompatActivity {
    private EditText editMealText;
    private EditText editCalorieText;
    private Button enterMealButton;

    private InputMealViewModel viewModel;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_meal);
        viewModel = new ViewModelProvider(this).get(InputMealViewModel.class);
        editMealText = findViewById(R.id.InputMealName);
        editCalorieText = findViewById(R.id.InputCalories);
        enterMealButton = findViewById(R.id.InputMealButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mAuth = FirebaseAuth.getInstance();
        //bottomNavigationView.setOnNavigationItemSelectedListener(this);

        enterMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mealName = editMealText.getText().toString().trim();
                String calorieText = editCalorieText.getText().toString().trim();
                if (viewModel != null) {
                //  viewModel.saveMealToFirebase();
                } else {
                 // viewModel = new ViewModelProvider(InputMealView.this).get(InputMealViewModel.class);
                  //viewModel.saveMealToFirebase();
                }
            }
        });  /*
        @Override
        public boolean onNavigationItemSelected (@NonNull MenuItem item){
            int id = item.getItemId();
            if (id == R.id.Home) {
                startActivity(new Intent(InputMealView.this, Home.class));
                return true;
            } else if (id == R.id.Recipe) {
                startActivity(new Intent(InputMealView.this, RecipeView.class));
                return true;
            } else if (id == R.id.Ingredients) {
                startActivity(new Intent(InputMealView.this, IngredientsView.class));
                return true;
            } else if (id == R.id.ShoppingList) {
                startActivity(new Intent(InputMealView.this, ShoppingListView.class));
                return true;
            }
            return false;
        } */
    }
}
