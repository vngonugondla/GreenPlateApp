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
        viewModel.setUser(new User("validUsername", "validPass"));
        assertTrue(viewModel.isValidInput());
    }

    @Test
    public void whenPasswordIsShort_thenAccountShouldBeInvalid() {
        viewModel.setUser(new User("username", "123"));
        assertFalse(viewModel.isValidInput());
    }
}