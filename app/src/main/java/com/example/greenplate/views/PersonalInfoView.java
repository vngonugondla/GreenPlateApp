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

import com.example.greenplate.R;
import com.example.greenplate.model.PersonalInfoModel;
import com.example.greenplate.model.User;
import com.example.greenplate.viewmodels.PersonalInfoViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PersonalInfoView extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {
    private EditText editHeight;
    private EditText editWeight;
    private EditText editGender;
    private Button enterUserButton;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");
    private PersonalInfoViewModel viewModel;

    private User user = User.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        viewModel = new ViewModelProvider(this).get(PersonalInfoViewModel.class);
        editHeight = findViewById(R.id.InputHeight);
        editWeight = findViewById(R.id.InputWeight);
        editGender = findViewById(R.id.InputGender);
        enterUserButton = findViewById(R.id.InputInfo);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Inside PersonalInfoView.java
        enterUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String height = editHeight.getText().toString().trim();
                String weight = editWeight.getText().toString().trim();
                String gender = editGender.getText().toString().trim();

                // Validate inputs
                if (height.isEmpty() || weight.isEmpty() || gender.isEmpty()) {
                    Toast.makeText(PersonalInfoView.this,
                            "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                } else if (gender == null || height == null || weight == null) {
                    gender = "Unknown";
                    height = "0";
                    weight = "0";
                } else {
                    // Retrieve the username (email) from the User singleton instance
                    String username = user.getUsername();
                    if (username != null && !username.isEmpty()) {
                        // Use only the part before the '@' symbol in the email as the key
                        // and remove any periods or other illegal characters
                        String sanitizedUsername = username.split("@")[0]
                                .replaceAll("[.#$\\[\\]]", "");

                        // Use the sanitized username to create a reference in your database
                        DatabaseReference userRef = root.child(sanitizedUsername);

                        PersonalInfoModel personalInfo = new PersonalInfoModel(height,
                                weight, gender);
                        userRef.setValue(personalInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(PersonalInfoView.this,
                                                "Information saved successfully",
                                                Toast.LENGTH_SHORT).show();
                                        // Inside PersonalInfoView.java, inside
                                        // the onSuccessListener of saving data
                                        Intent intent = new Intent(
                                                PersonalInfoView.this,
                                                InputMealView.class);
                                        startActivity(intent);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PersonalInfoView.this,
                                                "Failed to save information: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(PersonalInfoView.this,
                                "Username not set", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Home) {
            startActivity(new Intent(PersonalInfoView.this, Home.class));
            return true;
        } else if (id == R.id.Recipe) {
            startActivity(new Intent(PersonalInfoView.this, RecipeView.class));
            return true;
        } else if (id == R.id.Ingredients) {
            startActivity(new Intent(PersonalInfoView.this, IngredientsView.class));
            return true;
        } else if (id == R.id.ShoppingList) {
            startActivity(new Intent(PersonalInfoView.this, ShoppingListView.class));
            return true;
        } else if (id == R.id.InputMeal) {
            //startActivity(new Intent(PersonalInfoView.this, ShoppingListView.class));
            return true;
        }
        return false;
    }
}
