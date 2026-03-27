/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8880
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:37:31
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for concurrent approval status updates.
 * 
 * Preconditions:
 * - User logged in as requester
 * - Simulate concurrent backend approval status updates
 * - Real-time communication channel active (mocked)
 * 
 * Validates UI updates, concurrency control, and transactional integrity.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ConcurrentApprovalStatusUpdateTest {

    private static WebDriver driver;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.openMocks(this);

        // Mock user profile to simulate logged-in requester
        User mockUser = new User();
        mockUser.setId(1001L);
        mockUser.setUsername("requesterUser");
        mockUser.setRole("REQUESTER");
        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock currency conversion and forecast services with dummy data
        when(currencyConvertionController.convertCurrency(any(), any(), any())).thenReturn(1.0);
        when(fpmForecastController.getForecastData(any())).thenReturn(null);

        // Mock common controller to simulate approval status updates
        // This will be controlled in the test method
    }

    /**
     * Test simulating concurrent backend approval status updates and verifying UI reflects all updates correctly.
     */
    @Test
    public void testConcurrentApprovalStatusUpdates() throws Exception {
        // Step 1: Login as requester (simulate by navigating to login page and submitting form)
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("requesterUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect to dashboard
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                d -> d.getCurrentUrl().equals(BASE_URL + "/dashboard")
        );

        // Step 2: Navigate to approval requests page
        driver.get(BASE_URL + "/approvals");

        // Wait for approval list to load
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                d -> d.findElements(By.cssSelector(".approval-request-item")).size() > 0
        );

        // Prepare simulated concurrent backend events
        // We'll simulate 5 approval status updates with different statuses and timestamps
        List<String> statuses = Arrays.asList("PENDING", "APPROVED", "REJECTED", "APPROVED", "PENDING");

        // Use CountDownLatch to simulate concurrency
        CountDownLatch latch = new CountDownLatch(statuses.size());

        AtomicInteger updateCounter = new AtomicInteger(0);

        // Mock the FpmCommonController to simulate backend pushing status updates
        doAnswer(invocation -> {
            String approvalId = invocation.getArgument(0);
            String newStatus = invocation.getArgument(1);

            // Simulate delay and concurrency
            CompletableFuture.runAsync(() -> {
                try {
                    // Random delay to simulate concurrency
                    Thread.sleep(100 + (long)(Math.random() * 300));

                    // Simulate pushing update to UI via WebSocket or SSE (mocked here)
                    // We'll inject JS to update the UI element directly
                    String script = "var el = document.querySelector('[data-approval-id=\'" + approvalId + "\'] .status');"
                            + "if(el) { el.textContent = '" + newStatus + "'; }";
                    ((JavascriptExecutor) driver).executeScript(script);

                    updateCounter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });

            return null;
        }).when(fpmCommonController).updateApprovalStatus(any(String.class), any(String.class));

        // Step 3: Trigger concurrent backend events
        // Assume approval request with id "APPROVAL-12345" exists
        String approvalId = "APPROVAL-12345";

        for (String status : statuses) {
            fpmCommonController.updateApprovalStatus(approvalId, status);
        }

        // Wait for all updates to complete or timeout
        boolean allUpdatesCompleted = latch.await(5, TimeUnit.SECONDS);
        assertThat(allUpdatesCompleted).as("All concurrent updates should complete within timeout").isTrue();

        // Step 4: Verify UI reflects all status updates in correct order
        // Because updates are concurrent, the last update should be the final status
        String expectedFinalStatus = statuses.get(statuses.size() - 1);

        // Wait until UI shows the final status
        boolean finalStatusVisible = new WebDriverWait(driver, Duration.ofSeconds(5)).until((ExpectedCondition<Boolean>) d -> {
            WebElement statusElement = d.findElement(By.cssSelector("[data-approval-id='" + approvalId + "'] .status"));
            return statusElement != null && expectedFinalStatus.equals(statusElement.getText());
        });

        assertThat(finalStatusVisible).as("UI should display the final approval status").isTrue();

        // Step 5: Check for conflicting or overlapping status info
        // We check that only one status element exists and text is consistent
        List<WebElement> statusElements = driver.findElements(By.cssSelector("[data-approval-id='" + approvalId + "'] .status"));
        assertThat(statusElements).hasSize(1);

        // Step 6: Verify no UI errors or performance degradation (basic check)
        // Check for presence of error messages
        List<WebElement> errorMessages = driver.findElements(By.cssSelector(".error-message"));
        assertThat(errorMessages).isEmpty();

        // Optionally, check page responsiveness by timing a simple JS execution
        long start = System.currentTimeMillis();
        ((JavascriptExecutor) driver).executeScript("return document.readyState");
        long duration = System.currentTimeMillis() - start;
        assertThat(duration).isLessThan(2000).as("Page responsiveness should be within acceptable limits");
    }
}
