package com.example.greenplate.viewmodels;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class IngredientsViewModel extends ViewModel {

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Pantry");
    private User userInfo;

    public IngredientsViewModel() {
        userInfo = User.getInstance();
    }

    public boolean isIngredientExists;
    public boolean checkIngredientExists(String ingredientName) {
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
                        if (quantityStr != null && !quantityStr.isEmpty()) {
                            // Parse the quantity string to check if it's a nonzero value
                            double quantity = Double.parseDouble(quantityStr);
                            isIngredientExists = quantity > 0;
                        } else {
                            isIngredientExists = false; // Quantity is either null or empty
                        }
                    } else {
                        // Ingredient does not exist in pantry
                        isIngredientExists = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                    //Toast.makeText(IngredientsView.this,
                     //       "Error checking ingredient: " + error.getMessage(),
                      //      Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Return the flag indicating whether the ingredient exists or not
        return isIngredientExists;
    }

    /*public boolean checkIngredientExists(String ingredientName) {
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

            // Create a CountDownLatch with initial count 1
            CountDownLatch latch = new CountDownLatch(1);

            // Create a variable to store the result
            AtomicBoolean exists = new AtomicBoolean(false);

            // Check if the ingredient exists
            ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    exists.set(snapshot.exists());
                    latch.countDown(); // Decrease the latch count when the data is available
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                    exists.set(false); // Set the result to false in case of cancellation
                    latch.countDown(); // Ensure the latch count is decreased
                }
            });

            try {
                // Wait for the latch to count down to zero (i.e., until onDataChange or onCancelled is called)
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Return the result after waiting for onDataChange or onCancelled to be called
            return exists.get();
        }

        return false; // Return false if username is null or empty
    }*/



    /*public boolean checkIngredientExists(String ingredientName) {
        // Retrieve the username (email) from the User singleton instance
        String username = userInfo.getUsername();
        final CountDownLatch latch = new CountDownLatch(1); // Create a latch to wait for the result

        if (username != null && !username.isEmpty()) {
            // Use only the part before the '@' symbol in the email as the key
            // and remove any periods or other illegal characters
            String sanitizedUsername = username.split("@")[0]
                    .replaceAll("[.#$\\[\\]]", "");

            // Use the sanitized username to create a reference in your database
            DatabaseReference userRef = root.child(sanitizedUsername);

            // Create a reference to the ingredient in the user's pantry using the ingredient name as the key
            DatabaseReference ingredientRef = userRef.child(ingredientName);

            // Check if the ingredient exists
            ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Ingredient exists, check if quantity is nonzero
                    boolean isIngredientExists = snapshot.exists() && snapshot.child("quantity").getValue() != null;
                    latch.countDown(); // Release the latch after obtaining the result
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                    latch.countDown(); // Release the latch in case of cancellation
                }
            });

            try {
                latch.await(); // Wait for the latch to be released
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }*/


}
