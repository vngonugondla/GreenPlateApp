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

public class ShoppingListModel {
    private String shoppingItemName;
    private String quantity;

    public ShoppingListModel() {

    }
    public ShoppingListModel(String shoppingItemName, String quantity) {
        this.shoppingItemName = shoppingItemName;
        this.quantity = quantity;
    }
    public String getShoppingItemName() {
        return shoppingItemName;
    }

    public void setShoppingItemName(String shoppingItemName) {
        this.shoppingItemName = shoppingItemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
