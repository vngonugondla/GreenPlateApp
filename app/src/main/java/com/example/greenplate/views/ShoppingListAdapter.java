package com.example.greenplate.views;

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
import com.example.greenplate.model.User;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {

   /* private Context context;
    private ArrayList<ShoppingListModel> list;
    private OnShoppingClickListener onShoppingClickListener;


    private DatabaseReference shoppingListRef;

    public ShoppingListAdapter(ArrayList<ShoppingListModel> shoppingListItems, DatabaseReference reference
                               ) {
        this.list = shoppingListItems;
        this.shoppingListRef = reference;
    } */

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

        private CheckBox itemNameCheckBox;
        private TextView quantityTextView;
        private Button increaseButton;
        private Button decreaseButton;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameCheckBox = itemView.findViewById(R.id.SLItemTextView);
            quantityTextView = itemView.findViewById(R.id.SLQuantityTextView);
            increaseButton = itemView.findViewById(R.id.SLIncreaseButton);
            decreaseButton = itemView.findViewById(R.id.SLDecreaseButton);
        }

        public void bind(ShoppingListModel item) {
            itemNameCheckBox.setText(item.getShoppingItemName());
            itemNameCheckBox.setChecked(false);
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

        public boolean isChecked() {
            return itemNameCheckBox.isChecked();
        }

        public String getItemName() {
            return itemNameCheckBox.getText().toString();
        }

        public String getQuantity() {
            return quantityTextView.getText().toString();
        }

    /*
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoppinglist_item,
                parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ShoppingListModel shoppingListModel = list.get(position);
        Log.d("ADAPTER", shoppingListModel.getShoppingItemName());
        Log.d("ADAPTER", Integer.toString(shoppingListModel.getQuantity()));
        holder.shoppingListItems.setText(shoppingListModel.getShoppingItemName());
        holder.quantities.setText(Integer.toString(shoppingListModel.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox shoppingListItems;
        private TextView quantities;

        private Button increaseSLButton;
        private Button decreaseSLButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shoppingListItems = itemView.findViewById(R.id.textShoppingListItems);
            quantities = itemView.findViewById(R.id.textShoppingListQuantities);
        }
    }

    public interface OnShoppingClickListener {
        void onShoppingClick(ShoppingListModel shoppingListModel);
    }*/


    }
}
