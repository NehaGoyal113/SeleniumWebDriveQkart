package com.seleniumwebdriver.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SearchResult {

    private final WebElement parentElement;

    public SearchResult(WebElement searchResultElement) {
        this.parentElement = searchResultElement;
    }

    /**
     * Returns product title from the card.
     * ✅ Retries on StaleElementReferenceException
     */
    public String getTitleOfResult() {
        int attempts = 0;
        while (attempts < 3) {
            try {
                return parentElement.findElement(By.xpath(".//p")).getText();
            } catch (StaleElementReferenceException e) {
                attempts++;
                System.out.println("Stale element on attempt " + attempts + ", retrying...");
            }
        }
        throw new RuntimeException("getTitleOfResult: element stale after 3 attempts");
    }

    /**
     * Clicks the 'Size chart' button on the product card.
     * ✅ Replaced Thread.sleep(3000) with WebDriverWait
     */
    public boolean openSizeChart(WebDriver driver) {
        try {
            WebElement button = parentElement.findElement(
                    By.xpath(".//button[normalize-space()='Size chart']")
            );
            button.click();

            // Wait for size chart modal/table to become visible
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

            return true;
        } catch (Exception e) {
            System.out.println("Exception while opening Size chart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes size chart modal using ESC key.
     * ✅ Replaced deprecated synchronized(driver).wait() with WebDriverWait
     */
    public boolean closeSizeChart(WebDriver driver) {
        try {
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();

            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.invisibilityOfElementLocated(By.tagName("table")));

            return true;
        } catch (Exception e) {
            System.out.println("Exception while closing size chart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if 'Size chart' button exists in the product card.
     * ✅ Fixed: original compared "SIZE CHART" but button text is "Size chart"
     */
    public boolean verifySizeChartExists() {
        try {
            WebElement element = parentElement.findElement(
                    By.xpath(".//button[normalize-space()='Size chart']")
            );
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates size chart table headers and body match expected data.
     * ✅ Uses WebDriverWait instead of assuming modal is already open
     */
    public boolean validateSizeChartContents(
            List<String> expectedTableHeaders,
            List<List<String>> expectedTableBody,
            WebDriver driver) {

        try {
            WebElement modal = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.className("MuiDialog-paperScrollPaper")
                    ));

            WebElement table = modal.findElement(By.tagName("table"));

            // ── Validate headers ──────────────────────────────────────────
            List<WebElement> headers = table
                    .findElement(By.tagName("thead"))
                    .findElements(By.tagName("th"));

            for (int i = 0; i < expectedTableHeaders.size(); i++) {
                String actual = headers.get(i).getText();
                if (!expectedTableHeaders.get(i).equals(actual)) {
                    System.out.printf("Header mismatch [%d] Expected: %s | Actual: %s%n",
                            i, expectedTableHeaders.get(i), actual);
                    return false;
                }
            }

            // ── Validate body ─────────────────────────────────────────────
            List<WebElement> bodyRows = table
                    .findElement(By.tagName("tbody"))
                    .findElements(By.tagName("tr"));

            for (int i = 0; i < expectedTableBody.size(); i++) {
                List<WebElement> cells = bodyRows.get(i).findElements(By.tagName("td"));
                for (int j = 0; j < expectedTableBody.get(i).size(); j++) {
                    String actual = cells.get(j).getText();
                    if (!expectedTableBody.get(i).get(j).equals(actual)) {
                        System.out.printf("Body mismatch [%d][%d] Expected: %s | Actual: %s%n",
                                i, j, expectedTableBody.get(i).get(j), actual);
                        return false;
                    }
                }
            }
            return true;

        } catch (Exception e) {
            System.out.println("Error validating size chart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if size dropdown exists within the product card.
     * ✅ Fixed: searches parentElement scope, not entire driver
     */
    public boolean verifyExistenceOfSizeDropdown() {
        try {
            return parentElement
                    .findElement(By.xpath(".//select[@name='age']"))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}