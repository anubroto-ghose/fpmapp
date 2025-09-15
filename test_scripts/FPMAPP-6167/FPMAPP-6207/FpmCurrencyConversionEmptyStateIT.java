/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6207
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:50:15
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for regression test case REGRESSION-FPMAPP-6167-TC03
 * Ensures correct empty state handling when no INR to JPY currency conversions exist
 * and that UI respects role-based approval rules and audit trail visibility.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FpmCurrencyConversionEmptyStateIT {

    private static WebDriver driver;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private MockMvc mockMvc;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void teardownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        // Mock user profile for logged in user with approval roles
        Mockito.when(fpmUserProfileController.getCurrentUserRoles())
                .thenReturn(List.of("ROLE_APPROVER", "ROLE_ANALYST"));

        // Mock currency conversion history returns empty for INR->JPY
        Mockito.when(currencyConvertionController.getConversionHistory(eq("INR"), eq("JPY")))
                .thenReturn(Collections.emptyList());
    }

    @Test
    public void testEmptyStateDisplayedForNoInrToJpyConversions() throws Exception {
        // Assume user is authenticated - this will be part of Spring Security config outside scope

        // Use MockMvc to verify API returns empty list properly (backend contract)
        mockMvc.perform(get("/api/currency/conversions")
                        .param("from", "INR")
                        .param("to", "JPY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    assertTrue(json.equals("[]") || json.trim().isEmpty(), "Expected empty conversion history list");
                });

        // Navigate frontend UI: simulate login state already done
        // For test purposes, open local integration environment URL
        // e.g. http://localhost:8080/transactions/history
        String baseUrl = System.getProperty("integration.test.baseUrl", "http://localhost:8080");
        driver.get(baseUrl + "/transactions/history");

        // Filter for INR to JPY currency conversions
        WebElement fromCurrencySelect = driver.findElement(By.id("filter-from-currency"));
        fromCurrencySelect.click();
        WebElement inrOption = driver.findElement(By.xpath("//option[@value='INR']"));
        inrOption.click();

        WebElement toCurrencySelect = driver.findElement(By.id("filter-to-currency"));
        toCurrencySelect.click();
        WebElement jpyOption = driver.findElement(By.xpath("//option[@value='JPY']"));
        jpyOption.click();

        // Click filter/apply button
        WebElement applyFilterButton = driver.findElement(By.id("apply-filter"));
        applyFilterButton.click();

        // Wait for results to reload asynchronously
        // Use explicit wait
        try {
            Thread.sleep(1500); // In real project use WebDriverWait, simplified here due to brevity
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Interrupted during wait");
        }

        // Validate empty state message displayed
        WebElement emptyStateMessage = driver.findElement(By.id("empty-state-message"));
        assertNotNull(emptyStateMessage, "Empty state message element must be present");
        String displayedMessage = emptyStateMessage.getText();
        assertEquals("No INR to JPY transactions found", displayedMessage.trim(), "Empty state message text mismatch");

        // Verify that no transaction entries appear
        List<WebElement> transactionEntries = driver.findElements(By.cssSelector(".transaction-entry"));
        assertTrue(transactionEntries.isEmpty(), "No transaction entries should be displayed");

        // Verification of audit trail and currency integration UI artifacts - verify elements not present
        // (e.g. no audit records displayed, no currency override banners)
        List<WebElement> auditTrailEntries = driver.findElements(By.cssSelector(".audit-trail-entry"));
        assertTrue(auditTrailEntries.isEmpty(), "Audit trail entries should not appear for empty state");

        List<WebElement> currencyOverrideBanners = driver.findElements(By.id("currency-override-alert"));
        assertTrue(currencyOverrideBanners.isEmpty(), "Currency override alerts should not be present in empty state");

        // Additional role-based UI checks
        WebElement delegationControls = driver.findElement(By.id("delegation-controls"));
        // Should be present but disabled or hidden if no data
        assertNotNull(delegationControls, "Delegation controls must be present in UI");
        assertFalse(delegationControls.isDisplayed() && delegationControls.isEnabled(), "Delegation controls should not be enabled in empty data state");
    }
}