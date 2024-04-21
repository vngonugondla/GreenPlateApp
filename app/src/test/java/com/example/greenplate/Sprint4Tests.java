package com.example.greenplate;
import static org.junit.Assert.*;

import com.example.greenplate.model.IngredientsModel;
import com.example.greenplate.model.RecipeModel;
import com.example.greenplate.model.ShoppingListModel;
import com.example.greenplate.viewmodels.IngredientsViewModel;
import com.example.greenplate.viewmodels.ShoppingListViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Sprint4Tests {
    private ShoppingListModel model;

    @Before
    public void setUp() {
        model = new ShoppingListModel("Bread","10");
    }
    @Test
    public void shoppingListModelInitialization() {
        ShoppingListModel shop = new ShoppingListModel("Test Recipe", "10");
        assertEquals("Test Recipe", shop.getShoppingItemName());
        assertEquals("10",shop.getQuantity());
    }
    @Test
    public void shoppingListTest1() {
        ShoppingListModel shop = new ShoppingListModel("Pepper", "1");
        shop.setShoppingItemName("Salt");
        assertEquals("Salt", shop.getShoppingItemName());
        shop.setQuantity("20");
        assertEquals("20",shop.getQuantity());
    }
    @Test
    public void ingredientModelIntialization() {
        IngredientsModel ingredient = new IngredientsModel();
        assertEquals("", ingredient.getIngredientName());
        assertEquals("", ingredient.getQuantity());
        assertEquals("", ingredient.getCalories());
        IngredientsModel ingredient2 = new IngredientsModel("Water","10","100","04/19/2024");
        assertEquals("Water", ingredient2.getIngredientName());
        assertEquals("10", ingredient2.getQuantity());
        assertEquals("100", ingredient2.getCalories());
        assertEquals("04/19/2024", ingredient2.getExpirationDate());
    }



}
