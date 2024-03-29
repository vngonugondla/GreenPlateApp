package com.example.greenplate.viewmodels;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.User;
import com.example.greenplate.views.IngredientsView;
import com.example.greenplate.views.InputMealView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class IngredientsViewModel extends ViewModel {
    //private MutableLiveData<List<Ingredient>> ingredientsLiveData;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Pantry");
    private User userInfo;

    public IngredientsViewModel() {
        userInfo = User.getInstance();
        //ingredientsLiveData = new MutableLiveData<>();
    }
/*
    public LiveData<List<Ingredient>> getIngredientsLiveData() {
        return ingredientsLiveData;
    }

    public void fetchIngredients(String userId) {
        DatabaseReference userRef = root.child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Ingredient> ingredients = new ArrayList<>();
                for (DataSnapshot ingredientSnapshot: dataSnapshot.getChildren()) {
                    Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                    ingredients.add(ingredient);
                }
                ingredientsLiveData.setValue(ingredients);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }*/

    public boolean isIngredientExists;

    public interface IngredientCheckCallback {
        void onCheckCompleted(boolean exists);
    }
    public void checkIngredientExists(String ingredientName, IngredientCheckCallback callback) {
        // Retrieve the username (email) from the User singleton instance
        String username = userInfo.getUsername();

        if (username != null && !username.isEmpty()) {
            // Use only the part before the '@' symbol in the email as the key
            // and remove any periods or other illegal characters
            String sanitizedUsername = username.split("@")[0]
                    .replaceAll("[.#$\\[\\]]", "");

            // Use the sanitized username to create a reference in your database
            DatabaseReference userRef = root.child(sanitizedUsername);

            // Create a reference to the ingredient in the user's pantry using the ingredient name as the key
            DatabaseReference ingredientRef = userRef.child(ingredientName);

            // Check if the ingredient exists synchronously
            ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Ingredient exists, check if quantity is nonzero
                        String quantityStr = snapshot.child("quantity").getValue(String.class);
                        Double quantity = Double.parseDouble(quantityStr);
                        if (quantity != null && quantity > 0) {
                            callback.onCheckCompleted(true); // Ingredient exists and has a positive quantity
                        } else {
                            callback.onCheckCompleted(false); // Quantity is null, zero, or the key does not exist
                        }
                    } else {
                        callback.onCheckCompleted(false); // Ingredient does not exist
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error - might want to call callback with false or specific error handling
                    // error
                }
            });
        } else {
            // Handle the case where username is not available - assuming ingredient doesn't exist
            callback.onCheckCompleted(false);
        }


    }

}
