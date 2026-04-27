package com.seleniumwebdriver.tests;

import com.seleniumwebdriver.base.BaseTest;
import com.seleniumwebdriver.pages.Home;
import com.seleniumwebdriver.pages.SearchResult;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;
import java.util.List;

public class SearchResultTest extends BaseTest {

    /**
     * TC03 - Verify search box works for valid and invalid keywords
     */
    @Parameters({"searchProduct", "invalidProduct"})
    @Test(priority = 3, groups = {"smoke"},
            description = "Verify functionality of search text box")
    public void verifySearchFunctionality(String searchProduct, String invalidProduct) {

        SoftAssert softAssert = new SoftAssert();

        Home homePage = new Home(driver);
        homePage.navigateToHomePage();

        // ── Valid search ──────────────────────────────────────────────────
        Assert.assertTrue(
                homePage.searchForProduct(searchProduct),
                "Search failed for: " + searchProduct
        );

        List<WebElement> results = homePage.getSearchResults();

        Assert.assertTrue(results.size() > 0,
                "No results found for: " + searchProduct);

        // Re-fetch on every iteration to avoid stale elements
        for (int i = 0; i < results.size(); i++) {
            List<WebElement> fresh = homePage.getSearchResults();
            if (i >= fresh.size()) break;

            String productName = new SearchResult(fresh.get(i)).getTitleOfResult();

            softAssert.assertTrue(
                    productName.toLowerCase().contains(searchProduct.toLowerCase()),
                    "Unexpected product: " + productName
            );
        }

        // ── Invalid search ────────────────────────────────────────────────
        Assert.assertTrue(
                homePage.searchForProduct(invalidProduct),
                "Search action failed for: " + invalidProduct
        );

        softAssert.assertTrue(
                homePage.isNoResultFound(),
                "'No products found' message not displayed"
        );

        List<WebElement> emptyResults = homePage.getSearchResults();
        softAssert.assertEquals(emptyResults.size(), 0,
                "Products should not appear for invalid search: " + invalidProduct);

        softAssert.assertAll();
    }

    /**
     * TC04 - Verify presence and content of Size Chart
     */
    @Parameters({"productName"})
    @Test(priority = 4, groups = {"regression"},
            description = "Verify the presence of Size Chart")
    public void verifySizeChart(String productName) {

        Home homePage = new Home(driver);
        homePage.navigateToHomePage();

        Assert.assertTrue(
                homePage.searchForProduct(productName),
                "Search failed for: " + productName
        );

        List<WebElement> searchResults = homePage.getSearchResults();
        Assert.assertTrue(searchResults.size() > 0, "No results found for: " + productName);

        List<String> expectedHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");

        List<List<String>> expectedBody = Arrays.asList(
                Arrays.asList("6",  "6",  "40", "9.8"),
                Arrays.asList("7",  "7",  "41", "10.2"),
                Arrays.asList("8",  "8",  "42", "10.6"),
                Arrays.asList("9",  "9",  "43", "11"),
                Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"),
                Arrays.asList("12", "12", "46", "12.6")
        );

        for (int i = 0; i < searchResults.size(); i++) {

            // Always re-fetch to avoid stale element
            List<WebElement> fresh = homePage.getSearchResults();
            if (i >= fresh.size()) break;

            SearchResult result = new SearchResult(fresh.get(i));

            Assert.assertTrue(result.verifySizeChartExists(),
                    "Size chart button missing on card " + i);

            Assert.assertTrue(result.verifyExistenceOfSizeDropdown(),
                    "Size dropdown missing on card " + i);

            Assert.assertTrue(result.openSizeChart(driver),
                    "Could not open size chart on card " + i);

            Assert.assertTrue(
                    result.validateSizeChartContents(expectedHeaders, expectedBody, driver),
                    "Size chart content mismatch on card " + i);

            Assert.assertTrue(result.closeSizeChart(driver),
                    "Could not close size chart on card " + i);
        }
    }
}