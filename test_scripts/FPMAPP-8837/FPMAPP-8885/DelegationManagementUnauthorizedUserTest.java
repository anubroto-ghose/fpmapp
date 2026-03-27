/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8885
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:34:15
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class DelegationManagementUnauthorizedUserTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:";

    private static final String APPROVER_USERNAME = "approverUser";
    private static final String UNAUTHORIZED_DELEGATE_USERNAME = "unauthorizedUser";

    @BeforeEach
    public void setUp() {
        // Setup ChromeDriver (headless for CI environments)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);

        // Mock logged-in approver user
        User approver = new User();
        approver.setUsername(APPROVER_USERNAME);
        approver.setRoles(Collections.singletonList("ROLE_APPROVER"));
        when(userProfileController.getCurrentUser()).thenReturn(approver);

        // Mock unauthorized delegate user (lacking delegation eligibility)
        User unauthorizedDelegate = new User();
        unauthorizedDelegate.setUsername(UNAUTHORIZED_DELEGATE_USERNAME);
        unauthorizedDelegate.setRoles(Collections.singletonList("ROLE_USER")); // no delegation role
        when(userProfileController.findUserByUsername(UNAUTHORIZED_DELEGATE_USERNAME)).thenReturn(unauthorizedDelegate);

        // Mock delegation eligibility check to return false for unauthorized user
        when(fpmCommonController.isUserEligibleForDelegation(UNAUTHORIZED_DELEGATE_USERNAME)).thenReturn(false);

        // Mock delegation logging and notification - verify no calls
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testPreventDelegationToUnauthorizedUser() {
        driver.get(BASE_URL + port + "/delegation-management");

        // Wait for delegation management page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationForm")));

        // Step 1: Navigate to delegation management interface - already done by URL

        // Step 2: Attempt to select an unauthorized user as delegate
        WebElement delegateInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("delegateUsername")));
        delegateInput.clear();
        delegateInput.sendKeys(UNAUTHORIZED_DELEGATE_USERNAME);

        // Simulate blur or selection event to trigger eligibility check
        delegateInput.sendKeys("\t");

        // Step 3: Define a time period for delegation
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        WebElement startDateInput = driver.findElement(By.id("delegationStartDate"));
        startDateInput.clear();
        startDateInput.sendKeys(startDate.format(formatter));

        WebElement endDateInput = driver.findElement(By.id("delegationEndDate"));
        endDateInput.clear();
        endDateInput.sendKeys(endDate.format(formatter));

        // Step 4: Submit the delegation request
        WebElement submitButton = driver.findElement(By.id("submitDelegation"));
        submitButton.click();

        // Expected Results:
        // - The system prevents delegation to the unauthorized user.
        // - An appropriate error message is displayed indicating lack of permissions.
        // - No delegation action is logged.
        // - No notification is sent to the unauthorized user.

        // Wait for error message
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationErrorMessage")));
        String errorText = errorMessage.getText();

        assertThat(errorText).containsIgnoringCase("not authorized").or().containsIgnoringCase("lack of permissions");

        // Verify no delegation logged
        verify(fpmCommonController, never()).logDelegationAction(any(), any(), any(), any());

        // Verify no notification sent
        verify(fpmCommonController, never()).sendDelegationNotification(any(), any());
    }
}
