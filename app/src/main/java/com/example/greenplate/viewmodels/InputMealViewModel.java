package com.example.greenplate.viewmodels;
import android.text.TextUtils;
import androidx.lifecycle.ViewModel;
import com.example.greenplate.model.InputMealModel;

public class InputMealViewModel extends ViewModel {
    private InputMealModel inputMealModel;

    public InputMealViewModel() {
        inputMealModel = new InputMealModel("",0);
    }

    public void setMealName(String name) {
        inputMealModel.setMealName(name);
    }

    public void setCalories(int calories) {
        inputMealModel.setCalories(calories);
    }

    public void setInputMealModel(InputMealModel meal) {
        if (meal.getMealName() != null) {
            inputMealModel.setMealName(meal.getMealName());
        }
        inputMealModel.setCalories(meal.getCalories());
    }
    public void saveMealToFirebase() {
        inputMealModel.saveToFirebase();
    }

}
