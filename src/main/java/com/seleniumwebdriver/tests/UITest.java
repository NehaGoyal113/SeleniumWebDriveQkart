package com.seleniumwebdriver.tests;

import com.seleniumwebdriver.base.BaseTest;
import com.seleniumwebdriver.pages.Home;
import com.seleniumwebdriver.pages.Login;
import com.seleniumwebdriver.pages.Register;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Set;

public class UITest extends BaseTest {

    // ── Shared helper ─────────────────────────────────────────────────────

    private void registerAndLogin(String username, String password) {
        Register reg = new Register(driver);
        reg.navigateToRegisterPage();
        Assert.assertTrue(reg.registerUser(username, password, true), "Registration Failed");
        lastGeneratedUserName = reg.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        Assert.assertTrue(login.login(lastGeneratedUserName, password), "Login Failed");
    }

    // ── TC09 ──────────────────────────────────────────────────────────────

    /**
     * TC09 - Verify Privacy Policy and Terms of Service open in new tabs
     */
    @Parameters({"username", "password"})
    @Test(priority = 9, groups = {"regression"},
            description = "Verify Privacy Policy and Terms of Service display correctly")
    public void verifyPrivacyPolicyAndTermsOfService(String username, String password) {

        registerAndLogin(username, password);

        Home home = new Home(driver);
        home.navigateToHomePage();

        String baseURL = driver.getCurrentUrl();

        // ── Privacy Policy ────────────────────────────────────────────────
        driver.findElement(By.linkText("Privacy policy")).click();

        // Parent tab URL must not change
        Assert.assertEquals(driver.getCurrentUrl(), baseURL,
                "Parent page URL changed after clicking Privacy Policy");

        Set<String> handles = driver.getWindowHandles();
        String[] tabs = handles.toArray(new String[0]);

        driver.switchTo().window(tabs[1]);

        Assert.assertEquals(
                driver.findElement(By.xpath("//*[@id='root']/div/div[2]/h2")).getText(),
                "Privacy Policy",
                "Incorrect Privacy Policy heading"
        );

        driver.switchTo().window(tabs[0]);

        // ── Terms of Service ──────────────────────────────────────────────
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        tabs    = handles.toArray(new String[0]);

        driver.switchTo().window(tabs[2]);

        Assert.assertEquals(
                driver.findElement(By.xpath("//*[@id='root']/div/div[2]/h2")).getText(),
                "Terms of Service",
                "Incorrect Terms of Service heading"
        );

        // Close child tabs, restore parent
        driver.close();
        driver.switchTo().window(tabs[1]);
        driver.close();
        driver.switchTo().window(tabs[0]);
    }

    // ── TC10 ──────────────────────────────────────────────────────────────

    /**
     * TC10 - Verify Contact Us form submission
     */
    @Parameters({"contactName", "contactEmail", "contactMessage"})
    @Test(priority = 10, groups = {"regression"},
            description = "Verify Contact Us form works correctly")
    public void verifyContactUsForm(String contactName, String contactEmail, String contactMessage) {

        Home home = new Home(driver);
        home.navigateToHomePage();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        driver.findElement(By.xpath("//input[@placeholder='Name']")).sendKeys(contactName);
        driver.findElement(By.xpath("//input[@placeholder='Email']")).sendKeys(contactEmail);
        driver.findElement(By.xpath("//input[@placeholder='Message']")).sendKeys(contactMessage);

        WebElement submitBtn = driver.findElement(
                By.xpath("//button[contains(text(),'Contact Now')]")
        );
        submitBtn.click();

        Assert.assertTrue(
                new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(ExpectedConditions.invisibilityOf(submitBtn)),
                "Contact Us form not submitted successfully"
        );
    }

    // ── TC11 ──────────────────────────────────────────────────────────────

    /**
     * TC11 - Verify advertisement links are clickable (login prerequisite)
     */
    @Parameters({"username", "password"})
    @Test(priority = 11, groups = {"smoke"},
            description = "Ensure advertisement links on QKart are clickable")
    public void verifyAdvertisementLinksClickable(String username, String password) {
        registerAndLogin(username, password);
        // Advertisement link click assertions to be added here
        // when ad element locators are confirmed
    }
}