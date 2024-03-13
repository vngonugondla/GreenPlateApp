package com.example.greenplate.viewmodels;

import android.text.TextUtils;

import androidx.lifecycle.ViewModel;
import com.example.greenplate.model.User;

public class AccountCreationViewModel extends ViewModel {

    private User user = User.getInstance() ;

    public AccountCreationViewModel() {
        user.setUsername("");
        user.setPassword("");
    }


    public void setUsername(String username) {
        user.setUsername(username);
    }


    public void setPassword(String password) {
        user.setPassword(password);
    }

    public boolean isValidInput() {
        //return !user.getUsername().isEmpty() && user.getUsername() != null && !user.getPassword().isEmpty() && user.getPassword() != null;

        String username = user.getUsername();
        String password = user.getPassword();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        }

        if (TextUtils.isEmpty(username.trim()) || TextUtils.isEmpty(password.trim())) {
            return false;
        }

        return password.length() >= 5;
    }

    public void setUser(User newUser) {
        if (newUser.getUsername() != null) {
            user.setUsername(newUser.getUsername());
        }

        if (newUser.getPassword() != null) {
            user.setPassword(newUser.getPassword());
        }
    }

    public void createAccount() {
        // placeholder, need to implement later
    }

}