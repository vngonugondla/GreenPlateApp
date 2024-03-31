package com.example.greenplate;

import org.junit.Before;
import org.junit.Test;
import com.example.greenplate.model.RecipeModel;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class Sprint3RecipeTests {
    private RecipeModel recipe;
    private Map<String, String> ingredients;

    @Before
    public void setUp() {
        ingredients = new HashMap<>();
        ingredients.put("Flour", "1000"); // Assume the unit is grams
        ingredients.put("Sugar", "500");
        recipe = new RecipeModel("Cake", ingredients, false);
    }

    @Test
    public void checkIngredientsSufficiency_whenSufficient_returnsTrue() {
        Map<String, Integer> availableIngredients = new HashMap<>();
        availableIngredients.put("Flour", 1500);
        availableIngredients.put("Sugar", 600);

        assertTrue(recipe.checkIngredientsSufficiency(availableIngredients));
    }

    @Test
    public void checkIngredientsSufficiency_whenInsufficient_returnsFalse() {
        Map<String, Integer> availableIngredients = new HashMap<>();
        availableIngredients.put("Flour", 900); // Not enough flour
        availableIngredients.put("Sugar", 600);

        assertFalse(recipe.checkIngredientsSufficiency(availableIngredients));
    }
    @Test
    public void recipeModelInitialization() {
        Map<String, String> ingredients = new HashMap<>();
        ingredients.put("Ingredient1", "2 cups");
        ingredients.put("Ingredient2", "1 tsp");

        RecipeModel recipe = new RecipeModel("Test Recipe", ingredients, true);

        // Verify that the RecipeModel object is initialized as expected
        assertEquals("Test Recipe", recipe.getRecipeName());
        assertEquals(ingredients, recipe.getIngredients());
        assertTrue(recipe.getHasEnoughIngredients());
    }
}
