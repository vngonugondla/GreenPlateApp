package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.example.greenplate.databinding.LoginScreenBinding;
import com.example.greenplate.R;
import com.example.greenplate.viewmodels.LoginViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginView extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText editTextLoginUsername;
    private EditText editTextLoginPassword;
    private Button buttonNewUser;
    private Button buttonLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginScreenBinding loginScreenBinding = DataBindingUtil.setContentView(this,
                R.layout.login_screen);
        loginScreenBinding.setLoginViewModel(new LoginViewModel());

        loginScreenBinding.executePendingBindings();


  
        editTextLoginUsername = findViewById(R.id.editTextLoginUsername);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        buttonNewUser = findViewById(R.id.new_user);
        buttonLogin = findViewById(R.id.login_button);

        mAuth = FirebaseAuth.getInstance();

        loginScreenBinding.loginButton.setOnClickListener(v -> {
            loginScreenBinding.getLoginViewModel().onLoginClicked();

            signIn(editTextLoginUsername.getText().toString().trim(),
                    editTextLoginPassword.getText().toString().trim());

            if (loginScreenBinding.getLoginViewModel().isInputDataValid()) {
                navigateToHomePage();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //navigate to the home screen using an intent
            navigateToHomePage();
        }
    }

    //user signs in
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, show successful login message
                            Toast.makeText(LoginView.this, "Account successfully created.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginView.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
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

    private void navigateToHomePage() {
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
