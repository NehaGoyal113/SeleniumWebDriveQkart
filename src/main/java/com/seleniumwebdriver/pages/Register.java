package com.seleniumwebdriver.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Timestamp;
import java.time.Duration;

public class Register {

    WebDriver driver;
    String url = "https://qkart-qa-web.labs.crio.do/register";

    public String lastGeneratedUsername;   // ✅ populated after registerUser()

    By username        = By.id("username");
    By password        = By.id("password");
    By confirmPassword = By.id("confirmPassword");
    By registerBtn     = By.className("button");

    public Register(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToRegisterPage() {
        driver.get(url);
    }

    public boolean registerUser(String user, String pass, boolean dynamic) {
        if (dynamic) {
            user = user + "_" + new Timestamp(System.currentTimeMillis()).getTime();
        }
        driver.findElement(username).sendKeys(user);
        driver.findElement(password).sendKeys(pass);
        driver.findElement(confirmPassword).sendKeys(pass);
        driver.findElement(registerBtn).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.urlContains("/login"));

        lastGeneratedUsername = user;    // ✅ stored here, read from test
        return true;
    }
}