package com.example.greenplate.views;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.example.greenplate.R;
import com.example.greenplate.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.greenplate.viewmodels.InputMealViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import android.content.Intent;
import android.view.MenuItem;
import com.example.greenplate.model.MealInfo;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class InputMealView extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {
    private EditText editMealText;
    private EditText editCalorieText;
    private Button enterMealButton;
    private EditText editDateText;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Meal");
    private InputMealViewModel viewModel;
    private String mealKey;

    private TextView userInfoTextView;
    private TextView calorieGoalTextView;
    private TextView dailyCalorieIntakeTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private DatabaseReference mealsRef;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_meal);

        viewModel = new ViewModelProvider(this).get(InputMealViewModel.class);
        editMealText = findViewById(R.id.InputMealName);
        editCalorieText = findViewById(R.id.InputCalories);
        editDateText = findViewById(R.id.InputDate);
        enterMealButton = findViewById(R.id.InputMealButton);
        userInfoTextView = findViewById(R.id.userInfoTextView);
        calorieGoalTextView = findViewById(R.id.calorieGoalTextView);
        dailyCalorieIntakeTextView = findViewById(R.id.dailyCalorieIntakeTextView);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        String username1 = User.getInstance().getUsername();
        String username = username1.split("@")[0].replaceAll("[.#$\\[\\]]", "");
        userRef = db.getReference().child("Users").child(username);
        mealsRef = db.getReference().child("Users").child(username).child("Meals");

        Button personalInfoButton = findViewById(R.id.personalInfoButton);

        // Set onClick listener for the button
        personalInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the personal information screen
                startActivity(new Intent(InputMealView.this, PersonalInfoView.class));
            }
        });
        enterMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mealName = editMealText.getText().toString().trim();
                String calorieText = editCalorieText.getText().toString().trim();
                String date = editDateText.getText().toString().trim();

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
                } else if (mealName == null || calorieText == null || date == null) {
                    mealName = "Nothing";
                    calorieText = "0";
                    date = "00/00/0000";
                }

                // Get the current user's username
                String username = User.getInstance().getUsername();
                if (username == null || username.isEmpty()) {
                    Toast.makeText(InputMealView.this, "User is not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sanitize the username to create a valid Firebase key
                String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]", "");
                setupViz1Button(sanitizedUsername);
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
        Button showInfoButton = findViewById(R.id.showInfoButton);
        showInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveUserInfoAndCalculateCalorieGoal();
                calculateAndDisplayDailyCalorieIntake();
            }
        });

    }

    private void retrieveUserInfoAndCalculateCalorieGoal() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    String height = dataSnapshot.child("height").getValue(String.class);
                    String weight = dataSnapshot.child("weight").getValue(String.class);

                    if (gender == null || height == null || weight == null) {
                        Toast.makeText(InputMealView.this, "One or more user information fields are missing.", Toast.LENGTH_SHORT).show();
                        return; // Exit if any of the fields are null
                    }

                    userInfoTextView.setText(String.format(Locale.US, "Gender: %s, Height: %s cm, Weight: %s kg", gender, height, weight));

                    try {
                        double calorieGoal = calculateCalorieGoal(gender, height, weight);
                        calorieGoalTextView.setText(String.format(Locale.US, "Calorie Goal: %.2f kcal", calorieGoal));
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(InputMealView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where user data is not found
                    userInfoTextView.setText("User information not available");
                    calorieGoalTextView.setText("N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error: " + databaseError.getMessage());
            }
        });
    }


    private double calculateCalorieGoal(String gender, String heightStr, String weightStr) {
        double bmr;
        try {
            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);

            if (gender.equalsIgnoreCase("Male")) {
                bmr = 66 + (13.75 * weight) + (5 * height) - (6.75 * 25); // Example age 25 used for BMR calculation
            } else if (gender.equalsIgnoreCase("Female")) {
                bmr = 655 + (9.56 * weight) + (1.85 * height) - (4.7 * 25); // Example age 25 used for BMR calculation
            } else {
                throw new IllegalArgumentException("Invalid gender specified.");
            }
            return bmr;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Height and weight must be valid numbers.");
        }
    }


    private void calculateAndDisplayDailyCalorieIntake() {
        Date currentDate = Calendar.getInstance().getTime();
        // Update this to match the date format stored in your Firebase database
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String currentDateStr = dateFormat.format(currentDate);

        mealsRef.orderByChild("date").equalTo(currentDateStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalCalories = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                        // Make sure to check for null here as well
                        Integer calories = mealSnapshot.child("calories").getValue(Integer.class);
                        if (calories != null) {
                            totalCalories += calories;
                        }
                    }
                }
                dailyCalorieIntakeTextView.setText("Daily Calorie Intake: " + totalCalories + " kcal");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error: " + databaseError.getMessage());
            }
        });
    }

    private void setupViz1Button(String currentUser) {

        Button viz1Button = findViewById(R.id.button);
        viz1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCaloricIntakeAndDisplayChart(currentUser);
                Log.d("Viz1Button", "Button clicked");
            }
        });
    }

    private void fetchCaloricIntakeAndDisplayChart(String currentUser) {
        if (currentUser == null) {
            Log.d("ChartDebug", "No user is currently signed in.");
            Toast.makeText(this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
            return; // Exit the method if no user is signed in
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(currentUser).child("Meals");

        Calendar calendar = Calendar.getInstance();
        // Adjust the date format here
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        calendar.add(Calendar.MONTH, -1);
        String startDate = sdf.format(calendar.getTime()); // one month ago
        String endDate = sdf.format(new Date()); // today

        Log.d("ChartDebug", "Querying database from " + startDate + " to " + endDate);

        ref.orderByChild("date").startAt(startDate).endAt(endDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TreeMap<String, Integer> dailyCaloricIntakeMap = new TreeMap<>();
                for (DataSnapshot mealSnapshot : snapshot.getChildren()) {
                    MealInfo meal = mealSnapshot.getValue(MealInfo.class);
                    if (meal != null && meal.getDate() != null) {
                        int calories = meal.getCalories();
                        dailyCaloricIntakeMap.put(meal.getDate(),
                                dailyCaloricIntakeMap.getOrDefault(meal.getDate(), 0) + calories);

                        Log.d("ChartDebug", "Found entry: Date=" + meal.getDate() + ", Calories=" + calories);
                    }
                }

                if (dailyCaloricIntakeMap.isEmpty()) {
                    Log.d("ChartDebug", "No data found for the specified period.");
                }

                List<Integer> dailyCaloricIntake = new ArrayList<>(dailyCaloricIntakeMap.values());
                List<String> dateLabels = new ArrayList<>(dailyCaloricIntakeMap.keySet());
                createCaloricIntakeChart(dateLabels, dailyCaloricIntake);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DBError", "Database query cancelled", error.toException());
            }
        });
    }




    private void createCaloricIntakeChart(List<String> dateLabels, List<Integer> dailyCaloricIntake) {
        Cartesian columnChart = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        for (int i = 0; i < dailyCaloricIntake.size(); i++) {
            // Use dateLabels for the x-axis
            data.add(new ValueDataEntry(dateLabels.get(i), dailyCaloricIntake.get(i)));
        }
        columnChart.data(data);

        columnChart.title("Daily Caloric Intake Over Past Month");
        columnChart.xAxis(0).title("Date");
        columnChart.yAxis(0).title("Calories");

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setChart(columnChart);
    }





    // will need to be updated once personal info screen gets created
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Home) {
            startActivity(new Intent(InputMealView.this, Home.class));
            return true;
        } else if (id == R.id.Recipe) {
            startActivity(new Intent(InputMealView.this, RecipeView.class));
            return true;
        } else if (id == R.id.InputMeal) {
            return true; //ensures we can navigate to input meal screen from input meal screen
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

