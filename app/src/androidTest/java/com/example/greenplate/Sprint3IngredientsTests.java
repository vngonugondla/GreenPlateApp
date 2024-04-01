package com.example.greenplate;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.greenplate.model.IngredientsModel;
import com.example.greenplate.viewmodels.IngredientsViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class Sprint3IngredientsTests {

    private IngredientsViewModel viewModel;

    @Before
    public void setUp() {
        // Initialize the ViewModel here. Depending on your ViewModel, you might need the context.
        // For example, if your ViewModel does not need the context directly for the tests provided, you might not need to retrieve it.
        // Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        viewModel = new IngredientsViewModel();
    }

    @Test
    public void testValidIngredient() {
        IngredientsModel validIngredient = new IngredientsModel("Tomato", "2kg", "100", "12/12/2024");
        assertTrue("Valid ingredient should be considered valid", viewModel.isValidIngredient(validIngredient));
    }

    @Test
    public void testIngredientWithMissingName() {
        IngredientsModel ingredientWithNoName = new IngredientsModel("", "1kg", "50", "01/01/2023");
        assertFalse("Ingredient with no name should be considered invalid", viewModel.isValidIngredient(ingredientWithNoName));
    }

    @Test
    public void testIngredientWithInvalidCalories() {
        // Assuming that "invalid" calories means non-numeric or negative values
        IngredientsModel invalidCaloriesIngredient = new IngredientsModel("Carrot", "500g", "-10", "01/02/2023");
        assertFalse("Ingredient with invalid calories should be considered invalid", viewModel.isValidIngredient(invalidCaloriesIngredient));
    }

    @Test
    public void testIngredientWithInvalidExpirationDate() {
        // Assuming specific logic to validate expiration dates, e.g., format or past dates
        IngredientsModel ingredientWithInvalidDate = new IngredientsModel("Milk", "1L", "150", "07/08/1923");
        assertFalse("Ingredient with invalid expiration date should be considered invalid", viewModel.isValidIngredient(ingredientWithInvalidDate));
    }

    @Test
    public void testIngredientWithDefaultExpirationDate() {
        IngredientsModel ingredientWithDefaultDate = new IngredientsModel("Bread", "500g", "300", "01/01/2025");
        assertTrue("Ingredient with default expiration date should be considered valid", viewModel.isValidIngredient(ingredientWithDefaultDate));
    }
}