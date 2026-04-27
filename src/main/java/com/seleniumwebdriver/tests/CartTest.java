package com.seleniumwebdriver.tests;

import com.seleniumwebdriver.base.BaseTest;
import com.seleniumwebdriver.pages.Checkout;
import com.seleniumwebdriver.pages.Home;
import com.seleniumwebdriver.pages.Login;
import com.seleniumwebdriver.pages.Register;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CartTest extends BaseTest
{

    // ── Shared helpers ────────────────────────────────────────────────────

    private void registerAndLogin(String username, String password) {
        Register reg = new Register(driver);
        reg.navigateToRegisterPage();
        Assert.assertTrue(reg.registerUser(username, password, true), "Registration Failed");
        lastGeneratedUserName = reg.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        Assert.assertTrue(login.login(lastGeneratedUserName, password), "Login Failed");
    }

    private void placeOrderAndVerify(Checkout checkout, String address) {
        checkout.addNewAddress(address);
        checkout.selectAddress(address);
        checkout.placeOrder();

        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlToBe("https://qkart-qa-web.labs.crio.do/thanks"));

        Assert.assertTrue(
                driver.getCurrentUrl().endsWith("/thanks"),
                "Order not placed successfully"
        );
    }

    // ── TC05 ──────────────────────────────────────────────────────────────

    /**
     * TC05 - Verify happy flow of buying products
     */
    @Parameters({"username","password",
            "product1Search","product1Name",
            "product2Search","product2Name","address"})
    @Test(priority = 5, groups = {"smoke"},
            description = "Verify Happy Flow of buying products")
    public void verifyHappyFlowPurchase(
            String username, String password,
            String product1Search, String product1Name,
            String product2Search, String product2Name,
            String address) {

        registerAndLogin(username, password);

        Home home = new Home(driver);
        home.navigateToHomePage();

        Assert.assertTrue(home.searchForProduct(product1Search), "Search failed: " + product1Search);
        home.addProductToCart(product1Name);

        Assert.assertTrue(home.searchForProduct(product2Search), "Search failed: " + product2Search);
        home.addProductToCart(product2Name);

        home.clickCheckout();

        placeOrderAndVerify(new Checkout(driver), address);

        home.navigateToHomePage();
        Assert.assertTrue(home.performLogout(), "Logout Failed");
    }

    // ── TC06 ──────────────────────────────────────────────────────────────

    /**
     * TC06 - Verify cart quantities can be edited before checkout
     */
    @Parameters({"username","password",
            "cartProduct1Search","cartProduct1Name",
            "cartProduct2Search","cartProduct2Name","address"})
    @Test(priority = 6, groups = {"regression"},
            description = "Verify that cart can be edited")
    public void verifyCartEditing(
            String username, String password,
            String cartProduct1Search, String cartProduct1Name,
            String cartProduct2Search, String cartProduct2Name,
            String address) {

        registerAndLogin(username, password);

        Home home = new Home(driver);
        home.navigateToHomePage();

        Assert.assertTrue(home.searchForProduct(cartProduct1Search), "Search failed: " + cartProduct1Search);
        home.addProductToCart(cartProduct1Name);

        Assert.assertTrue(home.searchForProduct(cartProduct2Search), "Search failed: " + cartProduct2Search);
        home.addProductToCart(cartProduct2Name);

        // Edit quantities: increase → remove → reset
        home.changeProductQuantityInCart(cartProduct1Name, 2);
        home.changeProductQuantityInCart(cartProduct2Name, 0);  // remove
        home.changeProductQuantityInCart(cartProduct1Name, 1);

        home.clickCheckout();
        placeOrderAndVerify(new Checkout(driver), address);

        home.navigateToHomePage();
        Assert.assertTrue(home.performLogout(), "Logout Failed");
    }

    // ── TC07 ──────────────────────────────────────────────────────────────

    /**
     * TC07 - Verify insufficient wallet balance error on checkout
     */
    @Parameters({"username","password",
            "expensiveProductSearch","expensiveProductName","address"})
    @Test(priority = 7, groups = {"regression"},
            description = "Verify insufficient balance error at checkout")
    public void verifyInsufficientBalanceError(
            String username, String password,
            String expensiveProductSearch, String expensiveProductName,
            String address) {

        registerAndLogin(username, password);

        Home home = new Home(driver);
        home.navigateToHomePage();

        Assert.assertTrue(home.searchForProduct(expensiveProductSearch),
                "Search failed: " + expensiveProductSearch);
        home.addProductToCart(expensiveProductName);

        // Push quantity beyond wallet balance
        home.changeProductQuantityInCart(expensiveProductName, 10);

        home.clickCheckout();

        Checkout checkout = new Checkout(driver);
        checkout.addNewAddress(address);
        checkout.selectAddress(address);
        checkout.placeOrder();

        Assert.assertTrue(
                checkout.verifyInsufficientBalanceMessage(),
                "Insufficient balance message not displayed"
        );
    }

    // ── TC08 ──────────────────────────────────────────────────────────────

    /**
     * TC08 - Verify cart persists when same URL opened in new tab
     */
    @Parameters({"username","password","product1Search","product1Name"})
    @Test(priority = 8, groups = {"smoke"},
            description = "Verify cart contents persist in new tab")
    public void verifyCartPersistsInNewTab(
            String username, String password,
            String product1Search, String product1Name)
            throws InterruptedException {

        registerAndLogin(username, password);

        Home home = new Home(driver);
        home.navigateToHomePage();

        Assert.assertTrue(home.searchForProduct(product1Search), "Search failed: " + product1Search);
        home.addProductToCart(product1Name);

        String currentURL = driver.getCurrentUrl();

        // Open new tab via Privacy Policy link
        driver.findElement(By.linkText("Privacy policy")).click();

        Set<String> handles = driver.getWindowHandles();
        String[] tabs = handles.toArray(new String[0]);

        // Switch to new tab and navigate to cart page
        driver.switchTo().window(tabs[1]);
        driver.get(currentURL);

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("qkart"));

        Assert.assertTrue(
                home.verifyCartContents(Arrays.asList(product1Name)),
                "Cart not retained in new tab"
        );

        // Close new tab, return to original
        driver.close();
        driver.switchTo().window(tabs[0]);
    }
}
