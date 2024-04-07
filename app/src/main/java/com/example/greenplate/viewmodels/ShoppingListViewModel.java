package com.example.greenplate.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.IngredientsModel;
import com.example.greenplate.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShoppingListViewModel extends ViewModel {
    //private MutableLiveData<List<Ingredient>> ingredientsLiveData;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("ShoppingList");
    private User userInfo;

    private boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private boolean isCaloriesValid(String calories) {
        try {
            int cal = Integer.parseInt(calories);
            return cal >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public boolean isValidIngredient(IngredientsModel ingredient) {
        return isNameValid(ingredient.getIngredientName())
                && isCaloriesValid(ingredient.getCalories())
                && isExpirationDateValid(ingredient.getExpirationDate());
    }

    private boolean isExpirationDateValid(String expirationDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false); // Don't allow dates like 02/30/2020
        try {
            Date date = sdf.parse(expirationDate);
            return !date.before(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    public void checkIngredientExists(String ingredientName, IngredientsViewModel.IngredientCheckCallback callback) {
        // Retrieve the username (email) from the User singleton instance
        String username = userInfo.getUsername();

        if (username != null && !username.isEmpty()) {
            // Use only the part before the '@' symbol in the email as the key
            // and remove any periods or other illegal characters
            String sanitizedUsername = username.split("@")[0]
                    .replaceAll("[.#$\\[\\]]", "");

            // Use the sanitized username to create a reference in your database
            DatabaseReference userRef = root.child(sanitizedUsername);

            // Create a reference to the ingredient in the user's pantry using the ingredient
            // name as the key
            DatabaseReference ingredientRef = userRef.child(ingredientName);

            // Check if the ingredient exists synchronously
            ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Object quantityObj = snapshot.child("quantity").getValue();
                        Double quantity = null;
                        if (quantityObj instanceof Long) {
                            quantity = ((Long) quantityObj).doubleValue();
                        } else if (quantityObj instanceof String) {
                            try {
                                quantity = Double.parseDouble((String) quantityObj);
                            } catch (NumberFormatException e) {
                            }
                        }
                        if (quantity != null && quantity > 0) {
                            callback.onCheckCompleted(true);
                        } else {
                            callback.onCheckCompleted(false);
                        }
                    } else {
                        callback.onCheckCompleted(false);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //Handle error - might want to call callback with false or specific
                    // error handling
                }
            });
        } else {
            callback.onCheckCompleted(false);
        }


    }

    public ShoppingListViewModel() {
        userInfo = User.getInstance();
        //ingredientsLiveData = new MutableLiveData<>();
    }

    public interface IngredientCheckCallback {
        void onCheckCompleted(boolean exists);
    }
}
