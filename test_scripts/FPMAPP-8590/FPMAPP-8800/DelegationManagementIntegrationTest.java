/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8800
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:36:21
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

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
import org.mockito.MockitoAnnotations;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.AuditTrailService;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class DelegationManagementIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private AuditTrailService auditTrailService;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI environments)
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
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock user profile responses for authorized and unauthorized roles
        doReturn(createUserWithRole("manager", true))
            .when(userProfileController).getCurrentUser();
        doReturn(createUserWithRole("employee", false))
            .when(userProfileController).getUserByRole("employee");

        // Mock audit trail logging to do nothing
        doReturn(true).when(auditTrailService).logAction(any(), any(), any(), any());
    }

    private com.webapp.fpmapp.entities.User createUserWithRole(String role, boolean canDelegate) {
        com.webapp.fpmapp.entities.User user = new com.webapp.fpmapp.entities.User();
        user.setId(100L);
        user.setUsername(role + "User");
        user.setRole(role);
        user.setCanDelegate(canDelegate);
        return user;
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void testDelegationAllowedForAuthorizedRole() {
        // Arrange
        // Simulate login as manager (authorized role)
        doReturn(createUserWithRole("manager", true))
            .when(userProfileController).getCurrentUser();

        driver.get(baseUrl() + "/delegation-management");

        // Wait for the delegation form to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationForm")));

        // Act
        WebElement delegateeInput = driver.findElement(By.id("delegateeUsername"));
        delegateeInput.clear();
        delegateeInput.sendKeys("employeeUser");

        WebElement submitButton = driver.findElement(By.id("submitDelegation"));
        submitButton.click();

        // Wait for success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationSuccessMessage")));

        // Assert
        assertThat(successMsg.getText()).contains("Delegation successfully created");

        // Verify audit trail logged
        verify(auditTrailService, times(1)).logAction(any(Long.class), any(String.class), any(Long.class), any(String.class));

        // Verify delegation status visible
        WebElement statusElement = driver.findElement(By.id("delegationStatus"));
        assertThat(statusElement.getText()).contains("Active delegation to employeeUser");
    }

    @Test
    public void testDelegationBlockedForUnauthorizedRole() {
        // Arrange
        // Simulate login as employee (unauthorized role)
        doReturn(createUserWithRole("employee", false))
            .when(userProfileController).getCurrentUser();

        driver.get(baseUrl() + "/delegation-management");

        // Wait for the delegation form to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationForm")));

        // Act
        WebElement delegateeInput = driver.findElement(By.id("delegateeUsername"));
        delegateeInput.clear();
        delegateeInput.sendKeys("managerUser");

        WebElement submitButton = driver.findElement(By.id("submitDelegation"));
        submitButton.click();

        // Wait for error message
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationErrorMessage")));

        // Assert
        assertThat(errorMsg.getText()).contains("You are not authorized to delegate approval rights");

        // Verify audit trail NOT logged
        verify(auditTrailService, times(0)).logAction(any(Long.class), any(String.class), any(Long.class), any(String.class));
    }
}