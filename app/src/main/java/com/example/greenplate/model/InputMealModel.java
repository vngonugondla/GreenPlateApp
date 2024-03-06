package com.example.greenplate.model;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InputMealModel {
    private String mealName;
    private int calories;
    public InputMealModel(String mealName, int calories) {
        this.mealName = mealName;
        this.calories = calories;
    }

    public String getMealName() {
        return mealName;
    }

    public int getCalories() {
        return calories;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void saveToFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("meals");
        String key = databaseReference.push().getKey();

        if (key != null) {
            databaseReference.child(key).setValue(this);
        }
    }



}
