/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8895
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:27:19
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;

import org.springframework.boot.test.web.server.LocalManagementPort;

/**
 * Integration Selenium test for verifying override alerts are prominently displayed to admin users.
 * 
 * Preconditions:
 * - User logged in with admin role
 * - Override event triggered on approval request
 * 
 * This test mocks the override event and verifies UI alert display.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OverrideAlertAdminUITest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private final String adminUsername = "adminUser";
    private final String adminPassword = "adminPass123";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
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
    public void setup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock user profile service to return admin role
        when(fpmUserProfileController.getCurrentUserRole()).thenReturn("ADMIN");

        // Mock override event response
        when(fpmCommonController.getLatestOverrideEvent(any())).thenReturn(
                new OverrideEvent("OVERRIDE123", "Approval request #123 overridden by manager.", true));
    }

    /**
     * Test that override alert is displayed prominently to admin users immediately after override event.
     */
    @Test
    public void testOverrideAlertDisplayedToAdmin() {
        // Navigate to login page
        driver.get("http://localhost:" + port + "/login");

        // Perform login as admin user
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(adminUsername);
        passwordInput.sendKeys(adminPassword);
        loginButton.click();

        // Wait for dashboard/home page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Simulate triggering override event (mocked by service)
        // In real scenario, this might be a websocket event or polling
        // Here we simulate by navigating to a page that fetches override alerts
        driver.get("http://localhost:" + port + "/alerts");

        // Wait for override alert banner/modal to appear
        WebElement overrideAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("override-alert-banner")));

        // Verify alert is prominently visible
        assertThat(overrideAlert.isDisplayed()).isTrue();

        // Verify alert has highlight style (e.g., CSS class)
        String alertClass = overrideAlert.getAttribute("class");
        assertThat(alertClass).contains("alert-prominent");

        // Verify alert contains relevant override details
        String alertText = overrideAlert.getText();
        assertThat(alertText).contains("Approval request #123 overridden by manager");
        assertThat(alertText).contains("OVERRIDE123");

        // Verify no other less prominent alerts with same content
        // (Optional) Check that this alert is distinguishable

        // Verify non-admin user does NOT see the override alert
        // Logout admin
        WebElement logoutBtn = driver.findElement(By.id("logoutBtn"));
        logoutBtn.click();

        wait.until(ExpectedConditions.urlContains("/login"));

        // Login as non-admin user
        usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        passwordInput = driver.findElement(By.id("password"));
        loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("regularUser");
        passwordInput.sendKeys("userPass123");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to alerts page
        driver.get("http://localhost:" + port + "/alerts");

        // Verify override alert is NOT visible
        boolean alertPresent = driver.findElements(By.id("override-alert-banner")).size() > 0;
        assertThat(alertPresent).isFalse();
    }

    /**
     * Dummy DTO class to mock override event response
     */
    public static class OverrideEvent {
        private String overrideId;
        private String message;
        private boolean active;

        public OverrideEvent(String overrideId, String message, boolean active) {
            this.overrideId = overrideId;
            this.message = message;
            this.active = active;
        }

        public String getOverrideId() {
            return overrideId;
        }

        public String getMessage() {
            return message;
        }

        public boolean isActive() {
            return active;
        }
    }
}
