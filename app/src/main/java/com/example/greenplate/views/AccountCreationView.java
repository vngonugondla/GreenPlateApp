package com.example.greenplate.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.model.User;

import com.example.greenplate.viewmodels.AccountCreationViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AccountCreationView extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonCreateAccount;
    private AccountCreationViewModel viewModel;
    //create shared instance of firebaseAuth
    private FirebaseAuth mAuth;

    private TextView loginNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        viewModel = new ViewModelProvider(this).get(AccountCreationViewModel.class);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        loginNow = findViewById(R.id.login_now);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewModel.setUser(new User(
                        editTextUsername.getText().toString().trim(),
                        editTextPassword.getText().toString().trim()
                ));

                if (viewModel.isValidInput()) {
                    viewModel.createAccount();
                    //creates an account
                    createAccount(editTextUsername.getText().toString().trim(),
                            editTextPassword.getText().toString().trim());
                } else {
                    Toast.makeText(AccountCreationView.this,
                            "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //creates a new user account
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //If sign in is successful, show account created message
                            Toast.makeText(AccountCreationView.this,
                                    "Account successfully created.", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(AccountCreationView.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}