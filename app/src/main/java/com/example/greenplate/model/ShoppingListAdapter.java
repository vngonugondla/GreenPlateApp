package com.example.greenplate.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.model.ShoppingListModel;
import com.example.greenplate.R;
import com.example.greenplate.model.IngredientsModel;
import com.example.greenplate.model.User;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ShoppingListModel> list;
    private OnShoppingClickListener onShoppingClickListener;


    private DatabaseReference shoppingListRef;

    public ShoppingListAdapter(ArrayList<ShoppingListModel> shoppingListItems, DatabaseReference reference
                               ) {
        this.list = shoppingListItems;
        this.shoppingListRef = reference;
    }

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
    }
}
