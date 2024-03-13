package com.example.greenplate.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.util.Patterns;
import android.text.TextUtils;
import com.example.greenplate.BR;
import com.example.greenplate.model.User;


public class LoginViewModel extends BaseObservable {
    private User user;

    private String successMessage = "Login successful";
    private String errorMessage = "Username or Password is not valid";

    @Bindable
    private String toastMessage = null;

    public String getToastMessage() {
        return toastMessage;
    }

    private void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
        notifyPropertyChanged(BR.toastMessage);
    }

    public void setUserUsername(String username) {
        user.setUsername(username);
        notifyPropertyChanged(BR.userUsername);
    }

    @Bindable
    public String getUserUsername() {
        return user.getUsername();
    }

    @Bindable
    public String getUserPassword() {
        return user.getPassword();
    }

    public void setUserPassword(String password) {
        user.setPassword(password);
        notifyPropertyChanged(BR.userPassword);
    }

    public LoginViewModel() {
        user = User.getInstance();
    }

    public void onLoginClicked() {
        if (isInputDataValid()) {
            setToastMessage(successMessage);
        } else {
            setToastMessage(errorMessage);
        }
    }

    public boolean isInputDataValid() {
        String username = getUserUsername();
        String password = getUserPassword();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        }

        if (TextUtils.isEmpty(username.trim()) || TextUtils.isEmpty(password.trim())) {
            return false;
        }

        return password.length() >= 5;
    }

}
