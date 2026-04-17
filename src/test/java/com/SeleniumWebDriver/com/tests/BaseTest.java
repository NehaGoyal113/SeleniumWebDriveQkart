package com.SeleniumWebDriver.com.tests;
import com.SeleniumWebdriver.com.base.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.initDriver();
        driver.get("https://qkart-qa-web.labs.crio.do/");
    }

    @AfterMethod
    public void tearDown()
    {
        DriverFactory.quitDriver();
    }
}