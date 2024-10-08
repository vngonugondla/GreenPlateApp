package com.example.greenplate;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.greenplate.model.IngredientsModel;
import com.example.greenplate.model.ShoppingListModel;
import com.example.greenplate.viewmodels.IngredientsViewModel;
import com.example.greenplate.viewmodels.ShoppingListViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class Sprint4ShoppingTest {

    private ShoppingListViewModel viewModel;

    @Before
    public void setUp() {
        // Initialize the ViewModel here. Depending on your ViewModel, you might need the context.
        // For example, if your ViewModel does not need the context directly for the tests provided, you might not need to retrieve it.
        // Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        viewModel = new ShoppingListViewModel();
    }

    @Test
    public void testShoppingListName() {
        ShoppingListModel validShoppingList = new ShoppingListModel("Tomato", "10","","");
        assertTrue("Valid ingredient should be considered valid", viewModel.isNameValid(validShoppingList.getShoppingItemName()));
        validShoppingList.setShoppingItemName("");
        assertFalse(viewModel.isNameValid(validShoppingList.getShoppingItemName()));
        validShoppingList.setShoppingItemName("10");
        //assertFalse(viewModel.isNameValid(validShoppingList.getShoppingItemName()));
    }
    @Test
    public void testValidShoppingList2() {
        ShoppingListModel validShoppingList = new ShoppingListModel("Bread", "20","","");
        validShoppingList.setQuantity("50");
        assertEquals("50", validShoppingList.getQuantity());
    }

    @Test
    public void testShoppingList3() {
        ShoppingListViewModel viewModel = new ShoppingListViewModel();
        IngredientsModel ing = new IngredientsModel("Sauce", "10","100","04/21/2024");
        viewModel.isCaloriesValid(ing.getCalories());
    }

    @Test
    public void testShoppingList4() {
        ShoppingListModel shop = new ShoppingListModel("Pepper", "100","","");
        viewModel.isNameValid(shop.getShoppingItemName());
    }

    @Test
    public void testShoppingList5() {
        ShoppingListModel shop = new ShoppingListModel("Pepper", "100","","");
        viewModel.isNameValid(shop.getShoppingItemName());
    }
    @Test
    public void testShoppingList6() {
        ShoppingListModel shop2 = new ShoppingListModel("Water", "10","","");
        viewModel.isValidIngredient(new IngredientsModel(shop2.getShoppingItemName(), shop2.getQuantity(), "100","04/21/2024"));
    }


}
