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

    @Test
    public void whenUsernameAndPasswordAreValid_thenInputIsValid() {
        String validUsername = "validUser";
        String validPassword = "validPass"; // At least 5 characters
        viewModel.setUserUsername(validUsername);
        viewModel.setUserPassword(validPassword);
        assertTrue("When both username and password are valid, input should be considered valid.", viewModel.isInputDataValid());
    }

    @Test
    public void whenPasswordIsTooShort_thenInputIsInvalid() {
        String validUsername = "user";
        String shortPassword = "123"; // Less than 5 characters
        viewModel.setUserUsername(validUsername);
        viewModel.setUserPassword(shortPassword);
        assertFalse("When password is too short, input should be considered invalid.", viewModel.isInputDataValid());
    }
}
