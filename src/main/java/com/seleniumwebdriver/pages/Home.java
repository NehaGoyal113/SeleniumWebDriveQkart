package com.seleniumwebdriver.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Home {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final String url = "https://qkart-qa-web.labs.crio.do";

    // ── Locators ──────────────────────────────────────────────────────────
    private final By searchBox      = By.xpath("//input[contains(@name,'search') or @placeholder='Search']");
    private final By productCards   = By.xpath("//*[contains(@class,'MuiCard-root')]");
    private final By noResultsText  = By.xpath("//*[contains(text(),'No products found')]");
    private final By logoutButton   = By.className("MuiButton-text");
    private final By checkoutButton = By.xpath("//button[contains(text(),'Checkout')]");
    private final By cartItems      = By.xpath("//div[contains(@class,'cart')]/div/div/div[2]/div[1]");

    public Home(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Navigation ────────────────────────────────────────────────────────

    public void navigateToHomePage() {
        if (!driver.getCurrentUrl().equals(url)) {
            driver.get(url);
        }
    }

    // ── Page Info ─────────────────────────────────────────────────────────

    public String getPageTitle()  { return driver.getTitle(); }
    public String getCurrentURL() { return driver.getCurrentUrl(); }

    public boolean isBannerDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'FASTEST DELIVERY')]")
            )).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Auth ──────────────────────────────────────────────────────────────

    /**
     * Clicks Logout and waits for button to disappear
     */
    public boolean performLogout() {
        try {
            driver.findElement(logoutButton).click();
            wait.until(ExpectedConditions.invisibilityOfElementWithText(
                    By.className("css-1urhf6j"), "Logout"
            ));
            return true;
        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
            return false;
        }
    }

    // ── Search ────────────────────────────────────────────────────────────

    /**
     * Clears search box, types keyword, waits for results or no-result message.
     */
    public boolean searchForProduct(String product) {
        try {
            WebElement search = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(searchBox)
            );
            search.click();
            search.clear();
            search.sendKeys(product);

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(productCards),
                    ExpectedConditions.visibilityOfElementLocated(noResultsText)
            ));
            return true;
        } catch (Exception e) {
            System.out.println("Search failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns fresh list of product card WebElements — never cached/stale.
     */
    public List<WebElement> getSearchResults() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(productCards),
                    ExpectedConditions.visibilityOfElementLocated(noResultsText)
            ));
            return driver.findElements(productCards);
        } catch (Exception e) {
            System.out.println("No search results: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Returns true if 'No products found' message is displayed.
     */
    public boolean isNoResultFound() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(noResultsText)
            ).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Cart ──────────────────────────────────────────────────────────────

    /**
     * Finds product by name and clicks Add to Cart.
     * Waits until product appears in cart.
     */
    public boolean addProductToCart(String productName) {
        try {
            List<WebElement> cards = driver.findElements(productCards);

            for (WebElement card : cards) {
                String title = card.findElement(By.xpath(".//p")).getText();

                if (title.equalsIgnoreCase(productName)) {
                    card.findElement(By.xpath(".//button[contains(text(),'Add')]")).click();

                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                            String.format("//div[contains(@class,'cart')]//div[contains(text(),'%s')]",
                                    productName)
                    )));
                    return true;
                }
            }
            System.out.println("Product not found: " + productName);
            return false;

        } catch (Exception e) {
            System.out.println("Add to cart failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clicks the Checkout button.
     */
    public boolean clickCheckout() {
        try {
            driver.findElement(checkoutButton).click();
            return true;
        } catch (Exception e) {
            System.out.println("Checkout click failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Changes quantity of a product in cart.
     * Pass quantity=0 to remove the item completely.
     */
    public boolean changeProductQuantityInCart(String productName, int quantity) {
        try {
            List<WebElement> items = driver.findElements(cartItems);

            for (WebElement item : items) {
                if (item.getText().equalsIgnoreCase(productName)) {
                    WebElement parent = item.findElement(By.xpath(".."));

                    // Remove item completely
                    if (quantity == 0) {
                        parent.findElement(By.xpath(
                                ".//*[@data-testid='RemoveOutlinedIcon']"
                        )).click();
                        return true;
                    }

                    int currentQty = Integer.parseInt(
                            parent.findElement(By.xpath(
                                    ".//div[@data-testid='item-qty']"
                            )).getText()
                    );

                    while (currentQty != quantity) {
                        if (currentQty < quantity) {
                            parent.findElement(By.xpath(
                                    ".//*[@data-testid!='RemoveOutlinedIcon'" +
                                            " and @data-testid!='item-qty']"
                            )).click();
                        } else {
                            parent.findElement(By.xpath(
                                    ".//*[@data-testid='RemoveOutlinedIcon']"
                            )).click();
                        }

                        // Wait for quantity to update
                        final int previousQty = currentQty;
                        wait.until(driver -> {
                            String qty = parent.findElement(
                                    By.xpath(".//div[@data-testid='item-qty']")
                            ).getText();
                            return !qty.equals(String.valueOf(previousQty));
                        });

                        currentQty = Integer.parseInt(
                                parent.findElement(By.xpath(
                                        ".//div[@data-testid='item-qty']"
                                )).getText()
                        );
                    }
                    return true;
                }
            }
            System.out.println("Product not found in cart: " + productName);
            return false;

        } catch (Exception e) {
            System.out.println("Cart quantity update failed: " + e.getMessage());
            return quantity == 0;
        }
    }

    /**
     * Verifies cart contains all expected product names.
     */
    public boolean verifyCartContents(List<String> expectedCartContents) {
        try {
            WebElement cartParent = driver.findElement(By.className("cart"));
            List<WebElement> items = cartParent.findElements(By.className("css-zgtx0t"));

            List<String> actualContents = new ArrayList<>();
            for (WebElement item : items) {
                actualContents.add(
                        item.findElement(By.className("css-1gjj37g"))
                                .getText()
                                .split("\n")[0]
                                .trim()
                );
            }

            for (String expected : expectedCartContents) {
                if (!actualContents.contains(expected.trim())) {
                    System.out.println("Missing from cart: " + expected);
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            System.out.println("Cart verification failed: " + e.getMessage());
            return false;
        }
    }
}