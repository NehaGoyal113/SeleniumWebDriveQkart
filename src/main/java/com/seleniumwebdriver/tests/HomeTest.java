package com.seleniumwebdriver.tests;

import com.seleniumwebdriver.base.BaseTest;
import com.seleniumwebdriver.pages.Home;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomeTest extends BaseTest {

    @Test
    public void validateHomePageLoaded() {
        Home home = new Home(driver);
        home.navigateToHomePage();

        Assert.assertTrue(home.getCurrentURL().contains("qkart"));
        Assert.assertTrue(home.getPageTitle().contains("QKart"));
        Assert.assertTrue(home.isBannerDisplayed());
    }
}