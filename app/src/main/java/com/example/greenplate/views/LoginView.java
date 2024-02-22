package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.example.greenplate.databinding.LoginScreenBinding;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.LoginViewModel;
public class LoginView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginScreenBinding loginScreenBinding = DataBindingUtil.setContentView(this, R.layout.login_screen);
        loginScreenBinding.setLoginViewModel(new LoginViewModel());

        loginScreenBinding.executePendingBindings();

        loginScreenBinding.loginButton.setOnClickListener(v -> {
            loginScreenBinding.getLoginViewModel().onLoginClicked();
        });
    }

    @BindingAdapter({"toastMessage"})
    public static void runMe(View view, String message) {
        if (message != null) {
            Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onCreateAccountClicked(View view) {
        Intent intent = new Intent(this, AccountCreationView.class);
        startActivity(intent);
    }



}
