package com.seleniumwebdriver.tests;

import com.seleniumwebdriver.base.BaseTest;
import com.seleniumwebdriver.pages.Home;
import com.seleniumwebdriver.pages.Login;
import com.seleniumwebdriver.pages.Register;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class RegisterTest extends BaseTest {

    /**
     * TC01 - Verify new user can register and login successfully
     */
    @Parameters({"username", "password"})
    @Test(priority = 1, groups = {"smoke"},
            description = "Verify new user can register and login successfully")
    public void verifyUserRegistrationAndLogin(String username, String password) {

        // ── Register ──────────────────────────────────────────────────────
        Register register = new Register(driver);
        register.navigateToRegisterPage();

        Assert.assertTrue(
                register.registerUser(username, password, true),
                "User Registration Failed"
        );

        lastGeneratedUserName = register.lastGeneratedUsername;

        // ── Login ─────────────────────────────────────────────────────────
        Login login = new Login(driver);
        login.navigateToLoginPage();

        Assert.assertTrue(
                login.login(lastGeneratedUserName, password),
                "Login Failed"
        );

        // ── Logout ────────────────────────────────────────────────────────
        Home home = new Home(driver);

        Assert.assertTrue(
                home.performLogout(),
                "Logout Failed"
        );
    }

    /**
     * TC02 - Verify existing user cannot re-register
     */
    @Parameters({"username", "password"})
    @Test(priority = 2, groups = {"smoke"},
            description = "Verify existing user is not allowed to re-register")
    public void verifyDuplicateRegistrationNotAllowed(String username, String password) {

        Register register = new Register(driver);

        // Register fresh user
        register.navigateToRegisterPage();

        Assert.assertTrue(
                register.registerUser(username, password, true),
                "Initial Registration Failed"
        );

        String generatedUsername = register.lastGeneratedUsername;

        // Attempt to register same user again
        register.navigateToRegisterPage();

        Assert.assertFalse(
                register.registerUser(generatedUsername, password, false),
                "Duplicate registration should not be allowed"
        );
    }
}