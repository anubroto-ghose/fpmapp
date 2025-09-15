/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6224
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:37:24
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class DealSheetApprovalRoutingIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private FpmUserProfileController fpmUserProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setUpClass() {
        // Set path to chromedriver executable if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock currency conversion to a fixed rate
        when(currencyConvertionController.getCurrentRate("USD", "EUR"))
            .thenReturn(0.85);

        // Mock forecast data for deal sheet if needed
        when(fpmForecastController.getForecastForDeal(any(Long.class)))
            .thenReturn("Forecasted revenue: 1,000,000 USD");

        // Mock common controller methods if needed
        when(fpmCommonController.getCompanyName())
            .thenReturn("Test Bank Corp");

        // Setup roles and users
        // This is a simplification: in a real test inject test data to DB or mock service calls.
    }

    /**
     * Test scenario:
     * 1. Submit a deal sheet approval request exceeding approver A's threshold but within approver B's threshold.
     * 2. Verify automatic routing to approver B (next role in hierarchy with applicable threshold).
     * 3. Ensure approver A does not receive the approval task.
     * 4. Ensure approver B can see the pending approval.
     */
    @Test
    public void testDealSheetApprovalRoutingToCorrectApprover() throws InterruptedException {
        // Step 1: Submit deal sheet exceeding approver A's threshold
        driver.get(BASE_URL + "/login");

        // Log in as submitter
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("submitter1");
        passwordInput.sendKeys("password123");
        loginBtn.click();

        Thread.sleep(1000); // wait for login to complete

        // Navigate to deal sheet submission page
        driver.get(BASE_URL + "/dealsheets/new");

        // Fill out deal sheet form exceeding approver A's threshold
        WebElement amountInput = driver.findElement(By.id("dealAmount"));
        WebElement descriptionInput = driver.findElement(By.id("dealDescription"));
        WebElement submitBtn = driver.findElement(By.id("submitDealSheetBtn"));

        // Suppose approver A threshold is 100000, approver B threshold is 500000
        // Submit 200000, which is above approver A but within approver B
        amountInput.sendKeys("200000");
        descriptionInput.sendKeys("Corporate loan for expansion program");
        submitBtn.click();

        Thread.sleep(1000); // wait for submission

        // Verify submission success message
        WebElement successMsg = driver.findElement(By.id("submissionSuccessMsg"));
        assertThat(successMsg.getText()).contains("submitted successfully");

        // Step 2: Log out submitter
        WebElement logoutLink = driver.findElement(By.id("logoutLink"));
        logoutLink.click();
        Thread.sleep(500);

        // Step 3: Log in as approver A and verify NO pending approval assigned
        driver.get(BASE_URL + "/login");

        usernameInput = driver.findElement(By.id("username"));
        passwordInput = driver.findElement(By.id("password"));
        loginBtn = driver.findElement(By.id("loginBtn"));

        usernameInput.clear();
        passwordInput.clear();

        usernameInput.sendKeys("approverA");
        passwordInput.sendKeys("passwordA");
        loginBtn.click();

        Thread.sleep(1000);

        driver.get(BASE_URL + "/approvals/pending");

        List<WebElement> approvalsForA = driver.findElements(By.cssSelector(".approval-task"));
        assertThat(approvalsForA.size()).isEqualTo(0);

        // Step 4: Log out approver A
        logoutLink = driver.findElement(By.id("logoutLink"));
        logoutLink.click();
        Thread.sleep(500);

        // Step 5: Log in as approver B and verify pending approval assigned
        driver.get(BASE_URL + "/login");

        usernameInput = driver.findElement(By.id("username"));
        passwordInput = driver.findElement(By.id("password"));
        loginBtn = driver.findElement(By.id("loginBtn"));

        usernameInput.clear();
        passwordInput.clear();

        usernameInput.sendKeys("approverB");
        passwordInput.sendKeys("passwordB");
        loginBtn.click();

        Thread.sleep(1000);

        driver.get(BASE_URL + "/approvals/pending");

        List<WebElement> approvalsForB = driver.findElements(By.cssSelector(".approval-task"));
        assertThat(approvalsForB.size()).isGreaterThanOrEqualTo(1);

        boolean foundTestDeal = approvalsForB.stream().anyMatch(elem ->
            elem.getText().contains("Corporate loan for expansion program") &&
            elem.getText().contains("$200,000")
        );
        assertThat(foundTestDeal).isTrue();
    }
}