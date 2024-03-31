package com.example.greenplate.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.model.IngredientsModel;
import com.example.greenplate.model.User;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    private List<IngredientsModel> ingredientsList;

    private User user = User.getInstance();
    private DatabaseReference pantryRef; // Reference to the user's pantry in Firebase

    public IngredientsAdapter(List<IngredientsModel> ingredientsList, DatabaseReference pantryRef) {
        this.ingredientsList = ingredientsList;
        this.pantryRef = pantryRef;
    }



    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ingredient_item, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        IngredientsModel ingredient = ingredientsList.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
        //return 0;
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        private TextView ingredientNameTextView, quantityTextView;
        private Button increaseButton, decreaseButton;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientNameTextView = itemView.findViewById(R.id.ingredientNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
        }

        public void bind(IngredientsModel ingredient) {
            ingredientNameTextView.setText(ingredient.getIngredientName());
            quantityTextView.setText(String.valueOf(ingredient.getQuantity()));

            increaseButton.setOnClickListener(v -> adjustIngredientQuantity(ingredient, true));
            decreaseButton.setOnClickListener(v -> adjustIngredientQuantity(ingredient, false));
        }

        DatabaseReference userRef;
        private void adjustIngredientQuantity(IngredientsModel ingredient, boolean increase) {
            String username = user.getUsername();
            if (username != null && !username.isEmpty()) {
                // Use only the part before the '@' symbol in the email as the key
                // and remove any periods or other illegal characters
                String sanitizedUsername = username.split("@")[0].replaceAll("[.#$\\[\\]]", "");

                // Use the sanitized username to create a reference in your database
                userRef = pantryRef.child(sanitizedUsername);
                //DatabaseReference ingredientRef = userRef.child(ingredient.getIngredientName());
            }
            //double currentQuantity = 0;
            double currentQuantity = Double.parseDouble(ingredient.getQuantity());
            double newQuantity = increase ? currentQuantity + 1 : currentQuantity - 1;

            if (newQuantity <= 0) {
                // Remove the ingredient from Firebase
                userRef.child(ingredient.getIngredientName()).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(itemView.getContext(), ingredient.getIngredientName() + " removed.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Error removing " + ingredient.getIngredientName(), Toast.LENGTH_SHORT).show());
            } else {
                // Update the quantity in Firebase
                userRef.child(ingredient.getIngredientName()).child("quantity").setValue(newQuantity)
                        .addOnSuccessListener(aVoid -> {
                            // Update the displayed quantity
                            quantityTextView.setText(String.format("%.0f", newQuantity));
                            ingredient.setQuantity(String.valueOf(newQuantity));
                        })
                        .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Error updating quantity for " + ingredient.getIngredientName(), Toast.LENGTH_SHORT).show());
            }
        }
    }
}
