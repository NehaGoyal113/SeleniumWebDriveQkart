package com.seleniumwebdriver.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Login {

    WebDriver driver;

    String url = "https://qkart-qa-web.labs.crio.do/login";

    By username = By.id("username");
    By password = By.id("password");
    By loginBtn = By.className("button");
    By loggedUser = By.className("username-text");

    public Login(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToLoginPage() {
        driver.get(url);
    }

    public boolean login(String user, String pass) {

        driver.findElement(username).sendKeys(user);
        driver.findElement(password).sendKeys(pass);
        driver.findElement(loginBtn).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        wait.until(ExpectedConditions.visibilityOfElementLocated(loggedUser));

        return driver.findElement(loggedUser).getText().equals(user);
    }
}