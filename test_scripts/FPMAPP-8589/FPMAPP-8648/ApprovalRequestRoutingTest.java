/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8648
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:41:26
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration Selenium test for Approval Request Routing based on role thresholds.
 * 
 * Preconditions:
 * - Approval workflows configured with role thresholds for director, manager, others.
 * - Approval requests created with roleThreshold parameter.
 * 
 * This test mocks backend services to simulate routing logic and verifies UI reflects correct routing.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalRequestRoutingTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmTravelController travelController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController forecastController;

    @MockBean
    private FpmCommonController commonController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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
        // Mock user profiles for roles
        when(userProfileController.getUserByRole("director")).thenReturn(createUser("directorUser", "director"));
        when(userProfileController.getUserByRole("manager")).thenReturn(createUser("managerUser", "manager"));
        when(userProfileController.getUserByRole("others")).thenReturn(createUser("defaultUser", "employee"));

        // Mock approval routing logic in commonController
        when(commonController.routeApprovalRequest(any())).thenAnswer(invocation -> {
            Map<String, Object> request = invocation.getArgument(0);
            String roleThreshold = (String) request.getOrDefault("roleThreshold", "others");
            User routedUser;
            switch (roleThreshold.toLowerCase()) {
                case "director":
                    routedUser = createUser("directorUser", "director");
                    break;
                case "manager":
                    routedUser = createUser("managerUser", "manager");
                    break;
                default:
                    routedUser = createUser("defaultUser", "employee");
                    break;
            }
            Map<String, Object> response = new HashMap<>();
            response.put("routedUser", routedUser);
            response.put("roleThreshold", roleThreshold);
            response.put("log", "Routing decision logged for roleThreshold: " + roleThreshold);
            return response;
        });
    }

    private User createUser(String username, String role) {
        User user = new User();
        user.setUsername(username);
        user.setRole(role);
        user.setEmail(username + "@fpmapp.com");
        return user;
    }

    /**
     * Helper method to submit approval request via UI simulation.
     * 
     * @param roleThreshold the role threshold to submit
     */
    private void submitApprovalRequest(String roleThreshold) {
        driver.get("http://localhost:8080/approval-request");

        // Wait for page load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("roleThresholdInput")));

        WebElement roleInput = driver.findElement(By.id("roleThresholdInput"));
        roleInput.clear();
        roleInput.sendKeys(roleThreshold);

        WebElement submitBtn = driver.findElement(By.id("submitApprovalRequestBtn"));
        submitBtn.click();
    }

    /**
     * Helper method to get routed user displayed on UI after submission.
     * 
     * @return routed username
     */
    private String getRoutedUserFromUI() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("routedUserDisplay")));
        WebElement routedUserElem = driver.findElement(By.id("routedUserDisplay"));
        return routedUserElem.getText().trim();
    }

    /**
     * Helper method to get routing log displayed on UI.
     * 
     * @return routing log string
     */
    private String getRoutingLogFromUI() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("routingLogDisplay")));
        WebElement logElem = driver.findElement(By.id("routingLogDisplay"));
        return logElem.getText().trim();
    }

    @Test
    public void testApprovalRequestRoutingForDirector() {
        submitApprovalRequest("director");

        String routedUser = getRoutedUserFromUI();
        String routingLog = getRoutingLogFromUI();

        assertThat(routedUser).isEqualTo("directorUser");
        assertThat(routingLog).contains("roleThreshold: director");
    }

    @Test
    public void testApprovalRequestRoutingForManager() {
        submitApprovalRequest("manager");

        String routedUser = getRoutedUserFromUI();
        String routingLog = getRoutingLogFromUI();

        assertThat(routedUser).isEqualTo("managerUser");
        assertThat(routingLog).contains("roleThreshold: manager");
    }

    @Test
    public void testApprovalRequestRoutingForUndefinedRole() {
        submitApprovalRequest("employee");

        String routedUser = getRoutedUserFromUI();
        String routingLog = getRoutingLogFromUI();

        assertThat(routedUser).isEqualTo("defaultUser");
        assertThat(routingLog).contains("roleThreshold: employee");
    }

    @Test
    public void testNoRoutingToUnauthorizedRoles() {
        // Submit with a roleThreshold that should not route to unauthorized roles
        submitApprovalRequest("director");
        String routedUser = getRoutedUserFromUI();

        // Assert routed user role is director
        assertThat(routedUser).isEqualTo("directorUser");

        // Now check that manager or employee users are not routed
        assertThat(routedUser).doesNotContain("managerUser");
        assertThat(routedUser).doesNotContain("defaultUser");
    }

}
