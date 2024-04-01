package com.example.greenplate.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RecipeViewModel extends ViewModel {

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Cookbook");
    private User userInfo;

    public RecipeViewModel() {
        userInfo = User.getInstance();
    }

}