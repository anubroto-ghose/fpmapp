/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5269
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:39:53
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.controllers.FpmTravelController;
import com.webapp.fpmapp.services.FpmCommonController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openqa.selenium.By;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration test using Selenium WebDriver for travel expense approval request submission
 * and verifying notification receipt.
 * 
 * Tests the submission from UI and mocks backend services to simulate notification sending.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TravelApprovalNotificationIT {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private FpmTravelController travelController;

    @MockBean
    private FpmCommonController commonController;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test scenario:
     * 1. User logs in.
     * 2. User submits a new travel expense approval request.
     * 3. Backend mocks acceptance and returns success response.
     * 4. User receives a notification confirming submission.
     */
    @Test
    public void testTravelExpenseRequestSubmissionReceivesNotification() {
        // Arrange test data
        final String username = "financial_manager";
        final String password = "SecurePass123!";
        final String travelDestination = "New York";
        final String travelPurpose = "Client Meeting";
        final double estimatedCost = 1250.75;

        // Mock travelController to accept submission
        when(travelController.submitTravelApprovalRequest(any())).thenReturn(
                // Mocked response showing success + request ID
                "{\"status\":\"SUBMITTED\",\"requestId\":12345}"  
        );

        // Mock commonController or other service that triggers notification
        when(commonController.sendNotification(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);  // notification sent successfully

        String baseUrl = "http://localhost:" + port + "/";
        driver.get(baseUrl + "login");

        // Login flow
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("loginSubmit")).click();

        // Wait until redirected to dashboard or travel request page
        wait.until(ExpectedConditions.urlContains("dashboard"));

        // Navigate to travel request submission page
        driver.get(baseUrl + "travel/request/new");

        // Fill travel request form
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("travelDestination"))).sendKeys(travelDestination);
        driver.findElement(By.id("travelPurpose")).sendKeys(travelPurpose);
        driver.findElement(By.id("estimatedCost")).sendKeys(String.valueOf(estimatedCost));

        // Submit the form
        driver.findElement(By.id("submitTravelRequest")).click();

        // Verify submission success message (UI confirmation)
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submissionConfirmation")));
        String confirmationText = confirmationMessage.getText();
        assertTrue(confirmationText.contains("request has been submitted"), "Confirmation message missing or incorrect");

        // Wait for notification to appear (simulate push notification or UI alert)
        // Assuming notifications appear in an element with id 'notificationMessage'
        WebElement notification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationMessage")));
        String notificationText = notification.getText();
        assertTrue(notificationText.contains("Notification: Travel expense request submitted successfully"),
                "Notification not received or incorrect");
    }
}
