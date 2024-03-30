package com.example.greenplate.viewmodels;

import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.User;

public class PersonalInfoViewModel extends ViewModel {
    private User userInfo;

    public PersonalInfoViewModel() {
        userInfo = User.getInstance();
        userInfo.setHeight("");
        userInfo.setWeight("");
        userInfo.setGender("");
    }
    public void setHeight(String height) {
        userInfo.setHeight(height);
    }

    public void setWeight(String weight) {
        userInfo.setWeight(weight);
    }
    public void setGender(String gender) {
        userInfo.setGender(gender);
    }

    public void setPersonalInfo(User userInfo) {
        if (this.isValidInput(userInfo)) {
            userInfo.setHeight(userInfo.getHeight());
            userInfo.setWeight(userInfo.getWeight());
            userInfo.setGender(userInfo.getGender());

        }
    }
    public boolean isValidInput(User userInfo) {
        return userInfo != null
                && !TextUtils.isEmpty(userInfo.getGender())
                && !TextUtils.isEmpty(userInfo.getHeight())
                && !TextUtils.isEmpty(userInfo.getWeight());
    }


}
