package com.example.greenplate.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.model.ShoppingListModel;
import com.example.greenplate.R;
import com.example.greenplate.model.IngredientsModel;
import com.example.greenplate.model.User;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {

    private List<ShoppingListModel> shoppingList;

    private User user = User.getInstance();
    private DatabaseReference shoppingListRef; // Reference to the user's pantry in Firebase

    public ShoppingListAdapter(List<ShoppingListModel> shoppingList, DatabaseReference shoppingListRef) {
        this.shoppingList = shoppingList;
        this.shoppingListRef = shoppingListRef;
    }


    @NonNull
    @Override
    public ShoppingListAdapter.ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_shoppinglist_item, parent, false);
        return new ShoppingListAdapter.ShoppingListViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ShoppingListViewHolder holder, int position) {
        ShoppingListModel shoppingListItem = shoppingList.get(position);
        holder.bind(shoppingListItem);
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
        //return 0;
    }

    public class ShoppingListViewHolder extends RecyclerView.ViewHolder {

        private CheckBox itemNameTextView;
        private TextView quantityTextView;
        private Button increaseButton;
        private Button decreaseButton;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.SLItemTextView);
            quantityTextView = itemView.findViewById(R.id.SLQuantityTextView);
            increaseButton = itemView.findViewById(R.id.SLIncreaseButton);
            decreaseButton = itemView.findViewById(R.id.SLDecreaseButton);
        }

        public void bind(ShoppingListModel item) {
            itemNameTextView.setText(item.getShoppingItemName());
            quantityTextView.setText(String.valueOf(item.getQuantity()));

            increaseButton.setOnClickListener(v -> adjustIngredientQuantity(item, true));
            decreaseButton.setOnClickListener(v -> adjustIngredientQuantity(item, false));
        }

        private DatabaseReference userRef;

        private void adjustIngredientQuantity(ShoppingListModel item, boolean increase) {
            String username = user.getUsername();
            if (username != null && !username.isEmpty()) {
                // Use only the part before the '@' symbol in the email as the key
                // and remove any periods or other illegal characters
                String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]", "");

                // Use the sanitized username to create a reference in your database
                userRef = shoppingListRef.child(sanitizedUsername);
                //DatabaseReference ingredientRef = userRef.child(ingredient.getIngredientName());
            }
            //double currentQuantity = 0;
            double currentQuantity = Double.parseDouble(item.getQuantity());
            double newQuantity = increase ? currentQuantity + 1 : currentQuantity - 1;

            if (newQuantity <= 0) {
                // Remove the ingredient from Firebase
                userRef.child(item.getShoppingItemName()).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(itemView.getContext(),
                                item.getShoppingItemName() + " removed.",
                                Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(itemView.getContext(),
                                "Error removing " + item.getShoppingItemName(),
                                Toast.LENGTH_SHORT).show());
            } else {
                // Update the quantity in Firebase
                userRef.child(item.getShoppingItemName())
                        .child("quantity").setValue(newQuantity)
                        .addOnSuccessListener(aVoid -> {
                            // Update the displayed quantity
                            quantityTextView.setText(String.format("%.0f", newQuantity));
                            item.setQuantity(String.valueOf(newQuantity));
                        })
                        .addOnFailureListener(e -> Toast.makeText(itemView.getContext(),
                                "Error updating quantity for " + item.getShoppingItemName(),
                                Toast.LENGTH_SHORT).show());
            }
        }

        public DatabaseReference getUserRef() {
            return userRef;
        }

        public void setUserRef(DatabaseReference userRef) {
            this.userRef = userRef;
        }


    }
}
