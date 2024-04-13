package com.example.greenplate.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;

import java.util.ArrayList;
import java.util.Map;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ShoppingListModel> list;
    private OnShoppingClickListener onShoppingClickListener;

    public ShoppingListAdapter(Context context, ArrayList<ShoppingListModel> list,
                               OnShoppingClickListener listener) {
        this.context = context;
        this.list = list;
        this.onShoppingClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.shoppinglist_item,
                parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ShoppingListModel shoppingListModel = list.get(position);
        holder.shoppingListItems.setText(shoppingListModel.getShoppingItemName());
        holder.quantities.setText(shoppingListModel.getQuantity());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView shoppingListItems;
        private TextView quantities;

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
