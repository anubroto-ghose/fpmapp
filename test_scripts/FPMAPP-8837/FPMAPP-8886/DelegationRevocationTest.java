/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8886
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:33:46
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Selenium integration test for revocation of delegation by original approver.
 * 
 * Preconditions:
 * - A delegation exists with a valid delegate user and active time period.
 * - The original approver is logged into the system.
 * 
 * Test Steps:
 * 1. Navigate to the delegation management interface.
 * 2. Locate the active delegation.
 * 3. Revoke the delegation.
 * 
 * Expected Results:
 * - The delegation is successfully revoked.
 * - The delegate user no longer has approval rights.
 * - The revocation action is logged with user details and timestamps.
 * - The delegate user receives a notification about the revocation.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DelegationRevocationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private DelegationService delegationService;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
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

        // Mock logged in original approver user
        User originalApprover = new User();
        originalApprover.setId(1001L);
        originalApprover.setUsername("original.approver");
        originalApprover.setEmail("original.approver@bank.com");
        when(userProfileController.getLoggedInUser()).thenReturn(originalApprover);

        // Mock existing active delegation
        Delegation activeDelegation = new Delegation();
        activeDelegation.setId(5001L);
        activeDelegation.setApproverId(originalApprover.getId());
        activeDelegation.setDelegateUserId(2002L);
        activeDelegation.setStartTime(LocalDateTime.now().minusDays(1));
        activeDelegation.setEndTime(LocalDateTime.now().plusDays(1));
        activeDelegation.setActive(true);

        when(delegationService.findActiveDelegationByApproverId(originalApprover.getId()))
            .thenReturn(Optional.of(activeDelegation));

        // Mock revocation behavior
        doNothing().when(delegationService).revokeDelegation(activeDelegation.getId(), originalApprover.getId());

        // Mock notification sending
        doNothing().when(fpmCommonController).sendNotification(any(Long.class), any(String.class));
    }

    @Test
    public void testRevocationOfDelegationByOriginalApprover() {
        // Step 1: Login simulation (mocked user)
        User loggedInUser = userProfileController.getLoggedInUser();
        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getUsername()).isEqualTo("original.approver");

        // Step 2: Navigate to delegation management interface
        driver.get("http://localhost:8080/fpmapp/delegation-management");

        // Wait for page to load delegation table
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationTable")));

        // Step 3: Locate the active delegation row
        WebElement delegationTable = driver.findElement(By.id("delegationTable"));
        WebElement activeDelegationRow = delegationTable.findElements(By.tagName("tr")).stream()
            .filter(row -> {
                try {
                    WebElement statusCell = row.findElement(By.className("status"));
                    return statusCell.getText().equalsIgnoreCase("Active");
                } catch (Exception e) {
                    return false;
                }
            })
            .findFirst()
            .orElse(null);

        assertThat(activeDelegationRow).isNotNull();

        // Step 4: Click revoke button
        WebElement revokeButton = activeDelegationRow.findElement(By.className("btn-revoke"));
        revokeButton.click();

        // Confirm revocation modal
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmRevokeModal")));
        WebElement confirmButton = driver.findElement(By.id("confirmRevokeBtn"));
        confirmButton.click();

        // Wait for success notification
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationSuccess")));
        WebElement successNotification = driver.findElement(By.id("notificationSuccess"));
        assertThat(successNotification.getText()).contains("Delegation revoked successfully");

        // Verify backend revocation called
        verify(delegationService).revokeDelegation(any(Long.class), any(Long.class));

        // Verify delegate user no longer has approval rights
        Optional<Delegation> delegationAfterRevocation = delegationService.findActiveDelegationByApproverId(loggedInUser.getId());
        assertThat(delegationAfterRevocation).isEmpty();

        // Verify revocation logged
        verify(delegationService).logRevocationAction(any(Long.class), any(Long.class), any(LocalDateTime.class));

        // Verify notification sent to delegate user
        verify(fpmCommonController).sendNotification(any(Long.class), any(String.class));
    }

    // Mock Delegation entity and service for demonstration
    public static class Delegation {
        private Long id;
        private Long approverId;
        private Long delegateUserId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private boolean active;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getApproverId() { return approverId; }
        public void setApproverId(Long approverId) { this.approverId = approverId; }
        public Long getDelegateUserId() { return delegateUserId; }
        public void setDelegateUserId(Long delegateUserId) { this.delegateUserId = delegateUserId; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    public interface DelegationService {
        Optional<Delegation> findActiveDelegationByApproverId(Long approverId);
        void revokeDelegation(Long delegationId, Long revokedByUserId);
        void logRevocationAction(Long delegationId, Long userId, LocalDateTime timestamp);
    }
}
