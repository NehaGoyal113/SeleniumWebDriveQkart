package com.seleniumwebdriver.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Checkout {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final String url = "https://qkart-qa-web.labs.crio.do/checkout";

    // ── Locators ──────────────────────────────────────────────────────────
    private final By addNewAddressBtn = By.xpath("//button[normalize-space()='Add new address']");
    private final By addressTextArea  = By.xpath("//textarea");
    private final By addConfirmBtn    = By.xpath("//button[normalize-space()='ADD']");
    private final By addressItems     = By.xpath("//div[contains(@class,'address-item')]");
    private final By placeOrderBtn    = By.xpath("//button[normalize-space()='PLACE ORDER']");
    private final By snackbarAlert    = By.id("notistack-snackbar");

    public Checkout(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Navigation ────────────────────────────────────────────────────────

    public void navigateToCheckout() {
        if (!driver.getCurrentUrl().equals(url)) {
            driver.get(url);
        }
    }

    // ── Address ───────────────────────────────────────────────────────────

    /**
     * Clicks 'Add new address', fills textarea, clicks ADD.
     * Waits until address text appears on page.
     */
    public boolean addNewAddress(String addressString) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(addNewAddressBtn))
                    .click();

            WebElement addressBox = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(addressTextArea)
            );
            addressBox.clear();
            addressBox.sendKeys(addressString);

            wait.until(ExpectedConditions.elementToBeClickable(addConfirmBtn))
                    .click();

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(String.format("//*[contains(.,'%s')]", addressString))
            ));
            return true;

        } catch (Exception e) {
            System.out.println("Add address failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Selects the address matching the given text.
     */
    public boolean selectAddress(String addressToSelect) {
        try {
            List<WebElement> addresses = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(addressItems)
            );

            for (WebElement address : addresses) {
                if (address.getText().contains(addressToSelect)) {
                    address.click();
                    return true;
                }
            }

            System.out.println("Address not found: " + addressToSelect);
            return false;

        } catch (Exception e) {
            System.out.println("Select address failed: " + e.getMessage());
            return false;
        }
    }

    // ── Order ─────────────────────────────────────────────────────────────

    /**
     * Clicks PLACE ORDER button.
     */
    public boolean placeOrder() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(placeOrderBtn))
                    .click();
            return true;

        } catch (Exception e) {
            System.out.println("Place order failed: " + e.getMessage());
            return false;
        }
    }

    // ── Verification ──────────────────────────────────────────────────────

    /**
     * Verifies the insufficient balance snackbar is shown.
     */
    public boolean verifyInsufficientBalanceMessage() {
        try {
            WebElement alert = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(snackbarAlert)
            );
            String message = alert.getText();
            System.out.println("Snackbar: " + message);
            return message.contains("You do not have enough balance");

        } catch (Exception e) {
            System.out.println("Balance message check failed: " + e.getMessage());
            return false;
        }
    }
}