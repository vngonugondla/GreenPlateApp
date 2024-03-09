package com.example.greenplate.viewmodels;
import android.text.TextUtils;
import androidx.lifecycle.ViewModel;
import com.example.greenplate.model.InputMealModel;


public class InputMealViewModel extends ViewModel {
    private InputMealModel meal;

    public InputMealViewModel() {
        meal = new InputMealModel("",0);
    }

    public void setMealName(String name) {
        meal.setMealName(name);
    }

    public void setCalories(int calories) {
        meal.setCalories(calories);
    }

    public void setInputMealModel(InputMealModel newMeal) {
        if (newMeal.getMealName() != null) {
            meal.setMealName(newMeal.getMealName());
        }
        if (Integer.toString(newMeal.getCalories()) != null) {
            meal.setCalories(newMeal.getCalories());
        }
    }

    public String getMealName() {
        return meal.getMealName();
    }

    public int getCalories() {
        return meal.getCalories();
    }

    public boolean isValidInput() {

        String name = meal.getMealName();
        int calories = meal.getCalories();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(Integer.toString(calories))) {
            return false;
        }
        return true;
    }

}
