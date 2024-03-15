package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;
import com.example.greenplate.model.User;

public class AccountCreationViewModel extends ViewModel {

    private User user = User.getInstance();

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

        String username = user.getUsername();
        String password = user.getPassword();

        if (username == null || username.trim().isEmpty() || password == null
                || password.trim().isEmpty()) {
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