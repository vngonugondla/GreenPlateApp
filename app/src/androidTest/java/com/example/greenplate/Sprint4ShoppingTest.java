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
    public void testValidShoppingList() {
        ShoppingListModel validShoppingList = new ShoppingListModel("Tomato", "10");
        assertTrue("Valid ingredient should be considered valid", viewModel.isNameValid(validShoppingList.getShoppingItemName()));
    }

}