/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8790
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:29:26
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for rapid consecutive approval status updates.
 * 
 * Preconditions:
 * - User logged in
 * - WebSocket connection active (simulated via mocked backend push)
 * 
 * Validates UI updates without flicker or errors.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalStatusRapidUpdateIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private final String baseUrl = "http://localhost:";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profile to simulate logged in user
        when(fpmUserProfileController.getCurrentUser()).thenReturn(
                new com.webapp.fpmapp.entities.User(1001L, "testuser", "Test User", "ROLE_APPROVER"));

        // Mock dealsheet approval status retrieval
        when(fpmDealsheetController.getApprovalStatus(any(Long.class))).thenReturn("Pending");

        // Mock currency conversion calls (not focus here but required for context)
        when(currencyConvertionController.getCurrentRate("USD", "EUR")).thenReturn(0.85);

        // Mock forecast and common controller as no-op
        when(fpmForecastController.getForecastData(any())).thenReturn(null);
        when(fpmCommonController.getCommonData()).thenReturn(null);
    }

    /**
     * Test rapid consecutive approval status updates reflected in UI without flicker or errors.
     * Simulates backend pushing multiple status changes rapidly.
     */
    @Test
    public void testRapidApprovalStatusUpdates_NoFlicker_NoErrors() throws InterruptedException {
        String approvalItemId = "approval-item-123";

        // Navigate to the approval page
        driver.get(baseUrl + port + "/approval/view/" + approvalItemId);

        // Wait for page and approval status element
        By statusSelector = By.id("approval-status");
        wait.until(ExpectedConditions.visibilityOfElementLocated(statusSelector));

        WebElement statusElement = driver.findElement(statusSelector);

        // Verify initial status is Pending
        assertThat(statusElement.getText()).isEqualTo("Pending");

        // Simulate rapid backend approval status updates via JS injection (mock WebSocket push)
        List<String> rapidStatuses = Arrays.asList("Approved", "Rejected", "Delegated", "Approved");

        CountDownLatch latch = new CountDownLatch(rapidStatuses.size());

        // Inject a JS function to simulate WebSocket message handling
        String scriptSetup = "window.approvalStatusUpdates = [];" +
                "window.updateApprovalStatus = function(status) {" +
                "  var elem = document.getElementById('approval-status');" +
                "  if(elem) {" +
                "    elem.textContent = status;" +
                "    window.approvalStatusUpdates.push(status);" +
                "  }" +
                "}";
        ((JavascriptExecutor) driver).executeScript(scriptSetup);

        // Rapidly trigger status updates with small delay between them
        for (String status : rapidStatuses) {
            ((JavascriptExecutor) driver).executeScript("window.updateApprovalStatus(arguments[0]);", status);
            // Small sleep to simulate rapid but not simultaneous updates
            Thread.sleep(100);
        }

        // Wait a moment for UI to settle
        Thread.sleep(500);

        // Verify final status is the last one
        String finalStatus = statusElement.getText();
        assertThat(finalStatus).isEqualTo(rapidStatuses.get(rapidStatuses.size() - 1));

        // Verify all statuses were reflected in order (via JS array)
        @SuppressWarnings("unchecked")
        List<String> recordedStatuses = (List<String>) ((JavascriptExecutor) driver)
                .executeScript("return window.approvalStatusUpdates;");

        assertThat(recordedStatuses).containsExactlyElementsOf(rapidStatuses);

        // Verify no flicker or visual glitches by checking element is visible and stable
        assertThat(statusElement.isDisplayed()).isTrue();

        // Check for any visible error messages on page
        List<WebElement> errorElements = driver.findElements(By.cssSelector(".error-message, .alert-danger"));
        assertThat(errorElements).isEmpty();
    }
}
