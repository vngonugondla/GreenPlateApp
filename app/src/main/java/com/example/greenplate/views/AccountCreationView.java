package com.example.greenplate.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.greenplate.R;
import com.example.greenplate.model.User;

import com.example.greenplate.viewmodels.AccountCreationViewModel;

public class AccountCreationView extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonCreateAccount;
    private AccountCreationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        viewModel = new ViewModelProvider(this).get(AccountCreationViewModel.class);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewModel.setUser(new User(
                        editTextUsername.getText().toString().trim(),
                        editTextEmail.getText().toString().trim(),
                        editTextPassword.getText().toString().trim()
                ));

                if (viewModel.isValidInput()) {
                    viewModel.createAccount();
                } else {
                    Toast.makeText(AccountCreationView.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}