package com.example.greenplate.views;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.greenplate.R;
import com.example.greenplate.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import android.content.Intent;
import android.view.MenuItem;
import com.example.greenplate.model.MealInfo;
public class InputMealView extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener{
    private EditText editMealText;
    private EditText editCalorieText;
    private Button enterMealButton;
    private EditText editDateText;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Meal");
    private InputMealViewModel viewModel;
    private String mealKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_meal);
        viewModel = new ViewModelProvider(this).get(InputMealViewModel.class);
        editMealText = findViewById(R.id.InputMealName);
        editCalorieText = findViewById(R.id.InputCalories);
        editDateText = findViewById(R.id.InputDate);
        enterMealButton = findViewById(R.id.InputMealButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        enterMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mealName = editMealText.getText().toString().trim();
                final String calorieText = editCalorieText.getText().toString().trim();
                final String date = editDateText.getText().toString().trim();

                if (mealName.isEmpty()) {
                    Toast.makeText(InputMealView.this, "Meal Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (calorieText.isEmpty()) {
                    Toast.makeText(InputMealView.this, "Calories field cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (date.isEmpty()) {
                    Toast.makeText(InputMealView.this, "Date cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (date.length() != 10) {
                    Toast.makeText(InputMealView.this, "Date is in invalid format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the current user's username
                String username = User.getInstance().getUsername();
                if(username == null || username.isEmpty()) {
                    Toast.makeText(InputMealView.this, "User is not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sanitize the username to create a valid Firebase key
                String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]", "");

                // Locate the user's entry in the "Users" table and add the meal information
                DatabaseReference userMealsRef = FirebaseDatabase.getInstance().getReference("Users").child(sanitizedUsername).child("Meals");

                try {
                    int calorieValue = Integer.parseInt(calorieText);
                    MealInfo mealInfo = new MealInfo(mealName, calorieValue, date); // Assuming you have a MealInfo class that represents the structure of a meal

                    // Generate a unique key for the new meal entry
                    String mealKey = userMealsRef.push().getKey();
                    if (mealKey != null) {
                        userMealsRef.child(mealKey).setValue(mealInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(InputMealView.this, "Meal added to user profile", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(InputMealView.this, "Failed to add meal to user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(InputMealView.this, "Invalid Calorie Input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
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
        }


    }