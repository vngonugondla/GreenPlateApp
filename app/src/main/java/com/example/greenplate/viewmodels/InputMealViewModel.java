package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.InputMealModel;


public class InputMealViewModel extends ViewModel {
    private InputMealModel meal;

    public InputMealViewModel() {
        meal = new InputMealModel("", 0);
    }

    public void setMealName(String name) {
        meal.setMealName(name);
    }

    public void setCalories(int calories) {
        meal.setCalories(calories);
    }

    public void setInputMealModel(InputMealModel newMeal) {
        if (this.isValidInput(newMeal)) {
            meal.setMealName(newMeal.getMealName());
            meal.setCalories(newMeal.getCalories());
        }
    }
    public boolean isValidInput(InputMealModel newMeal) {
        if (newMeal == null || newMeal.getMealName() == null || newMeal.getMealName().isEmpty()) {
            return false;
        }
        return true;
    }

}
