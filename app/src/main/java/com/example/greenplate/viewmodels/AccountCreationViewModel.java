package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;
import com.example.greenplate.model.User;

public class AccountCreationViewModel extends ViewModel {

    private User user;

    public AccountCreationViewModel() {
        user = new User("", "", "");
    }


    public void setUsername(String username) {
        user.setUsername(username);
    }

    public void setEmail(String email) {
        user.setEmail(email);
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }

    public boolean isValidInput() {
        return !user.getUsername().isEmpty() && !user.getEmail().isEmpty() && !user.getPassword().isEmpty();
    }

    public void setUser(User newUser) {
        if (newUser.getUsername() != null) {
            user.setUsername(newUser.getUsername());
        }

        if (newUser.getEmail() != null) {
            user.setEmail(newUser.getEmail());
        }

        if (newUser.getPassword() != null) {
            user.setPassword(newUser.getPassword());
        }
    }

    public void createAccount() {
        // placeholder, need to implement later
    }
}