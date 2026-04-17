package com.SeleniumWebDriver.com.tests;

import com.SeleniumWebdriver.com.pages.HomePage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HomeTest extends BaseTest {

    @Test
    public void validateHomePageLoaded() {

        HomePage homePage = new HomePage(driver);

        // 1. Validate URL
        Assert.assertTrue(
                homePage.getCurrentURL().contains("qkart"),
                "URL is incorrect!"
        );

        // 2. Validate Title
        Assert.assertTrue(
                homePage.getPageTitle().toLowerCase().contains("qkart"),
                "Title is incorrect!"
        );

        // 3. Validate key element (Best practice )
        Assert.assertTrue(
                homePage.isLogoLinkDisplayed(),
                "Home page not loaded properly - Courses link missing!"
        );
    }
}