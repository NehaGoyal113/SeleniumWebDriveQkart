package com.SeleniumWebdriver.com.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {

    WebDriver driver;

    // Locators
    By text = By.xpath("//*[contains(text(),'FASTEST DELIVERY')]");

    // Constructor
    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    // Actions / Validations
    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }

    public boolean isLogoLinkDisplayed() {
        return driver.findElement(text).isDisplayed();
    }
}