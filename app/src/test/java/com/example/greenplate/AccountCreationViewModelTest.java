package com.example.greenplate;

import com.example.greenplate.model.User;
import com.example.greenplate.viewmodels.AccountCreationViewModel;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccountCreationViewModelTest {

    private AccountCreationViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new AccountCreationViewModel();
    }

    @Test
    public void whenGivenValidInput_thenAccountShouldBeValid() {
        viewModel.setUser(User.getInstance());
        String userName = "validUsername";
        String password = "validPass";
        User.getInstance().setUsername(userName);
        User.getInstance().setPassword(password);
        assertTrue(viewModel.isValidInput());
    }


    @Test
    public void whenPasswordIsShort_thenAccountShouldBeInvalid() {
        viewModel.setUser(User.getInstance());
        String userName = "username";
        String password = "123";
        User.getInstance().setUsername(userName);
        User.getInstance().setPassword(password);
        assertFalse(viewModel.isValidInput());
    }
    @Test
    public void whenPasswordIsExactlyFiveCharacters_thenShouldBeConsideredValid() {
        viewModel.setUser(User.getInstance());
        String userName = "username";
        String password = "12345";
        User.getInstance().setUsername(userName);
        User.getInstance().setPassword(password);
        assertTrue("Password with exactly 5 characters should be considered valid.", viewModel.isValidInput());
    }
}