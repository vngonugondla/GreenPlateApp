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

    private String calories;

    private String expiry;

    public ShoppingListModel() {

    }

    public ShoppingListModel(String shoppingItemName, String quantity, String calories, String expiry) {
        this.shoppingItemName = shoppingItemName;
        this.quantity = quantity;
        this.calories = calories;
        this.expiry = expiry;
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

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
    public String getExpiry() {
        return expiry;
    }
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

}
