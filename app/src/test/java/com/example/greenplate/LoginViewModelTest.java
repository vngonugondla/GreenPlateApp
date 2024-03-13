package com.example.greenplate;

import com.example.greenplate.viewmodels.LoginViewModel;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginViewModelTest {

    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new LoginViewModel();
    }

    @Test
    public void whenGivenValidCredentials_thenLoginShouldBeValid() {
        viewModel.setUserUsername("user");
        viewModel.setUserPassword("password");
        assertTrue(viewModel.isInputDataValid());
    }

    @Test
    public void whenPasswordIsShort_thenLoginShouldBeInvalid() {
        viewModel.setUserUsername("user");
        viewModel.setUserPassword("123");
        assertFalse(viewModel.isInputDataValid());
    }
}
