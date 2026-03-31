/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8923
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:06:23
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DelegationAuditTrailTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

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
    public void setupMocks() {
        // Mock user with delegation rights
        User delegator = new User();
        delegator.setId(1001L);
        delegator.setUsername("delegatorUser");
        delegator.setDelegationRights(true);

        User delegatee = new User();
        delegatee.setId(1002L);
        delegatee.setUsername("delegateeUser");
        delegatee.setDelegationRights(false);

        when(fpmUserProfileController.getUserByUsername("delegatorUser")).thenReturn(delegator);
        when(fpmUserProfileController.getUserByUsername("delegateeUser")).thenReturn(delegatee);

        // Mock audit log response after delegation
        when(fpmCommonController.getAuditLogByActionAndUsers("DELEGATION", "delegatorUser", "delegateeUser"))
            .thenReturn(Collections.singletonList(
                new AuditLogEntry("DELEGATION", "delegatorUser", "delegateeUser", Instant.now(), "Delegation for workload balancing", true)
            ));
    }

    @Test
    public void testDelegationActionIsLoggedWithFullMetadata() {
        // Preconditions: Login as delegator user
        driver.get("http://localhost:8080/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("delegatorUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard/homepage
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Assume request assigned to delegator is visible in requests list
        WebElement requestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr[data-request-id='REQ-12345']")));
        assertThat(requestRow).isNotNull();

        // Click delegate button
        WebElement delegateButton = requestRow.findElement(By.cssSelector("button.delegate-btn"));
        delegateButton.click();

        // Wait for delegation modal
        WebElement delegateModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateModal")));

        // Fill delegatee username and reason
        WebElement delegateeInput = delegateModal.findElement(By.id("delegateeUsername"));
        WebElement reasonInput = delegateModal.findElement(By.id("delegationReason"));
        WebElement submitDelegateBtn = delegateModal.findElement(By.id("submitDelegate"));

        delegateeInput.sendKeys("delegateeUser");
        reasonInput.sendKeys("Delegation for workload balancing");
        submitDelegateBtn.click();

        // Wait for success notification
        WebElement successNotification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".notification.success")));
        assertThat(successNotification.getText()).contains("Delegation successful");

        // Navigate to audit log page
        driver.get("http://localhost:8080/audit-log");

        // Filter audit log by delegation action and users
        WebElement actionFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("actionFilter")));
        WebElement delegatorFilter = driver.findElement(By.id("delegatorFilter"));
        WebElement delegateeFilter = driver.findElement(By.id("delegateeFilter"));
        WebElement filterButton = driver.findElement(By.id("filterBtn"));

        actionFilter.sendKeys("DELEGATION");
        delegatorFilter.sendKeys("delegatorUser");
        delegateeFilter.sendKeys("delegateeUser");
        filterButton.click();

        // Wait for audit log entries
        WebElement auditLogTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogTable")));
        List<WebElement> rows = auditLogTable.findElements(By.tagName("tr"));

        // There should be at least one delegation log entry
        assertThat(rows.size()).isGreaterThan(1); // header + entries

        // Validate the first delegation log entry
        WebElement firstEntry = rows.get(1);
        List<WebElement> cols = firstEntry.findElements(By.tagName("td"));

        String loggedAction = cols.get(0).getText();
        String loggedDelegator = cols.get(1).getText();
        String loggedDelegatee = cols.get(2).getText();
        String loggedTimestamp = cols.get(3).getText();
        String loggedReason = cols.get(4).getText();
        String loggedImmutableFlag = cols.get(5).getText();

        assertThat(loggedAction).isEqualToIgnoringCase("DELEGATION");
        assertThat(loggedDelegator).isEqualTo("delegatorUser");
        assertThat(loggedDelegatee).isEqualTo("delegateeUser");
        assertThat(loggedTimestamp).isNotEmpty();
        assertThat(loggedReason).isEqualTo("Delegation for workload balancing");
        assertThat(loggedImmutableFlag).isEqualToIgnoringCase("true");
    }

    // Inner class to mock audit log entry
    public static class AuditLogEntry {
        private String action;
        private String delegatorUser;
        private String delegateeUser;
        private Instant timestamp;
        private String reason;
        private boolean immutable;

        public AuditLogEntry(String action, String delegatorUser, String delegateeUser, Instant timestamp, String reason, boolean immutable) {
            this.action = action;
            this.delegatorUser = delegatorUser;
            this.delegateeUser = delegateeUser;
            this.timestamp = timestamp;
            this.reason = reason;
            this.immutable = immutable;
        }

        // Getters omitted for brevity
    }
}
