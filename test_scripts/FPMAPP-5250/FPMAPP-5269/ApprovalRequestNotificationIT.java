/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5269
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:15:53
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.services.FpmCommonController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ApprovalRequestNotificationIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private String baseUrl;

    @BeforeAll
    public static void setupClass() {
        // Initialize ChromeDriver with headless option for CI/CD environment
        System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver"); // adjust path or env var
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

    @Test
    public void testApprovalRequestSubmissionNotification() throws Exception {
        baseUrl = "http://localhost:" + port;

        // Mock the travel request submission endpoint to simulate success
        when(fpmTravelController.submitTravelRequest(any()))
            .thenReturn("TRAVEL_REQUEST_SUCCESS_ID_1234");

        // Mock notifications via fpmCommonController to simulate notification dispatch
        when(fpmCommonController.sendNotification(any(), any()))
            .thenReturn(true);

        // Navigate to login page
        driver.get(baseUrl + "/login");

        // Perform login with realistic banking user credentials
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("finManager01");
        passwordInput.sendKeys("SecureP@ssw0rd!");
        loginButton.click();

        // Wait for dashboard to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to Approval Request Form
        driver.get(baseUrl + "/approval/request/new");

        // Fill out the travel expense approval form realistically
        WebElement requestTypeSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("requestType")));
        requestTypeSelect.click();
        WebElement travelOption = driver.findElement(By.xpath("//option[@value='TRAVEL_EXPENSE']"));
        travelOption.click();

        WebElement destinationInput = driver.findElement(By.id("destination"));
        WebElement travelDateInput = driver.findElement(By.id("travelDate"));
        WebElement amountInput = driver.findElement(By.id("amount"));
        WebElement currencySelect = driver.findElement(By.id("currency"));
        WebElement submitButton = driver.findElement(By.id("submitApprovalRequest"));

        destinationInput.sendKeys("New York, USA");
        travelDateInput.sendKeys("2025-10-15");
        amountInput.sendKeys("1500");
        currencySelect.click();
        WebElement usdOption = driver.findElement(By.xpath("//option[@value='USD']"));
        usdOption.click();

        // Submit the approval request
        submitButton.click();

        // Wait for notification - assume notification panel appears on page
        WebElement notificationPanel = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("notificationPanel"))
        );

        // Verify that notification confirms submission
        String notificationText = notificationPanel.getText();
        assertThat(notificationText).contains("Request submitted successfully");
        assertThat(notificationText).contains("travel expense");

        // Additionally verify the mock interactions if possible (Spring Mockito verification could be used here)
        Mockito.verify(fpmTravelController).submitTravelRequest(any());
        Mockito.verify(fpmCommonController).sendNotification(any(), any());
    }
}
