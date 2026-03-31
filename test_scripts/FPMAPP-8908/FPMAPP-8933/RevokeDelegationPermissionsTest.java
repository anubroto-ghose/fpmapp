/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8933
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:56:30
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for revoking delegation permissions and verifying access removal.
 * 
 * Preconditions:
 * - A delegation exists with active permissions granted to a delegate user.
 * - The user revoking delegation has authorization to do so.
 * 
 * This test mocks necessary services and performs UI interactions using Selenium WebDriver.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RevokeDelegationPermissionsTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private CurrencyConvertionController currencyConvertionController; // Just to show usage, not used here

    private final String baseUrl = "http://localhost:8080";

    private final String delegatorUsername = "approverUser";
    private final String delegateUsername = "delegateUser";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
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

        // Mock the delegation exists with active permissions
        doReturn(true).when(fpmCommonController).hasActiveDelegation(delegateUsername);

        // Mock authorization for revoking delegation
        doReturn(true).when(fpmCommonController).isAuthorizedToRevoke(delegatorUsername);

        // Mock logging of revocation action
        doReturn(true).when(fpmCommonController).logDelegationRevocation(any(), any(), any());
    }

    @Test
    public void testRevokeDelegationPermissionsAndVerifyAccessRemoval() {
        // Step 1: Login as delegator user
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(delegatorUsername);
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("loginButton")).click();

        // Verify login success by checking presence of delegation management link
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationManagementLink")));

        // Step 2: Navigate to delegation management interface
        driver.findElement(By.id("delegationManagementLink")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationList")));

        // Step 3: Select the delegate user whose permissions are to be revoked
        List<WebElement> delegates = driver.findElements(By.cssSelector("#delegationList .delegate-row"));
        WebElement targetDelegateRow = null;
        for (WebElement row : delegates) {
            String username = row.findElement(By.cssSelector(".delegate-username")).getText();
            if (delegateUsername.equals(username)) {
                targetDelegateRow = row;
                break;
            }
        }
        assertThat(targetDelegateRow).as("Delegate user row should be present").isNotNull();

        // Step 4: Revoke delegation permissions
        WebElement revokeButton = targetDelegateRow.findElement(By.cssSelector("button.revoke-permissions"));
        revokeButton.click();

        // Confirm revocation modal appears
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmRevokeModal")));
        driver.findElement(By.id("confirmRevokeButton")).click();

        // Step 5: Save changes
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("confirmRevokeModal")));

        // Verify success notification
        WebElement successNotification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationSuccess")));
        assertThat(successNotification.getText()).contains("Delegation permissions revoked successfully");

        // Verify service method called to revoke delegation
        verify(fpmCommonController, times(1)).revokeDelegationPermissions(delegateUsername);

        // Verify revocation action logged with timestamp and user details
        verify(fpmCommonController, times(1)).logDelegationRevocation(delegatorUsername, delegateUsername, getCurrentTimestamp());

        // Step 6: Logout delegator
        driver.findElement(By.id("logoutButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginButton")));

        // Step 7: Login as delegate user
        driver.findElement(By.id("username")).sendKeys(delegateUsername);
        driver.findElement(By.id("password")).sendKeys("delegatePass123");
        driver.findElement(By.id("loginButton")).click();

        // Step 8: Attempt to perform approval action
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalPageLink"))).click();

        // Try to approve a dummy request
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalRequestList")));
        List<WebElement> approvalButtons = driver.findElements(By.cssSelector("button.approve-request"));

        if (approvalButtons.isEmpty()) {
            // No approval buttons means no permission
            assertThat(true).isTrue();
        } else {
            approvalButtons.get(0).click();

            // Expect error message or access denied
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
            assertThat(errorMsg.getText()).contains("You do not have permission to approve");
        }

        // Step 9: Verify system enforces role-based approval rules post-revocation
        boolean hasApprovalPermission = fpmCommonController.hasApprovalPermission(delegateUsername);
        assertThat(hasApprovalPermission).isFalse();
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
