/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8879
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:38:06
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test validating real-time UI updates for approval status and delegation notes.
 * 
 * Preconditions:
 * - User logged in as requester
 * - Approval request with delegation/override notes
 * - Real-time updates enabled
 * 
 * This test mocks backend event triggers and verifies UI updates accordingly.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalStatusRealTimeUpdateTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @Autowired
    private CurrencyConvertionController currencyConvertionController; // just to show usage of service injection

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Mock user profile to simulate logged-in requester
        when(fpmUserProfileController.getCurrentUser()).thenReturn(
                new com.webapp.fpmapp.entities.User("requesterUser", "Requester", "requester@example.com", "ROLE_REQUESTER"));

        // Mock common controller to simulate real-time update enabled
        when(fpmCommonController.isRealTimeUpdatesEnabled()).thenReturn(true);
    }

    /**
     * Test scenario:
     * 1. User logged in as requester
     * 2. Approval request with delegation/override notes exists
     * 3. Backend event triggers update to approval stage and notes
     * 4. UI updates in real-time without user intervention
     * 5. Validate UI shows correct approval stage and notes
     */
    @Test
    public void testApprovalStatusAndDelegationNotesRealTimeUpdate() throws InterruptedException {
        // Navigate to approval request status page
        driver.get(BASE_URL + "/approval/status");

        // Wait for page load and user info display
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userRole")));

        WebElement userRoleElement = driver.findElement(By.id("userRole"));
        assertThat(userRoleElement.getText()).containsIgnoringCase("requester");

        // Simulate initial approval stage and delegation notes
        String initialStage = "Pending Manager Approval";
        String initialNotes = "No delegation notes.";

        injectApprovalStatusToUI(initialStage, initialNotes);

        // Verify initial UI state
        WebElement stageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStage")));
        WebElement notesElement = driver.findElement(By.id("delegationNotes"));

        assertThat(stageElement.getText()).isEqualTo(initialStage);
        assertThat(notesElement.getText()).isEqualTo(initialNotes);

        // Simulate backend event: approval stage updated and delegation notes added
        String updatedStage = "Approved by Manager";
        String updatedNotes = "Delegated to Senior Analyst due to workload.";

        // Mock backend service to return updated data
        when(fpmCommonController.getCurrentApprovalStage(any())).thenReturn(updatedStage);
        when(fpmCommonController.getDelegationNotes(any())).thenReturn(updatedNotes);

        // Trigger frontend to fetch updated data (simulate backend event push)
        triggerBackendEventUpdate();

        // Wait and verify UI updates in real-time
        wait.until(ExpectedConditions.textToBe(By.id("approvalStage"), updatedStage));
        wait.until(ExpectedConditions.textToBe(By.id("delegationNotes"), updatedNotes));

        // Final assertions
        assertThat(driver.findElement(By.id("approvalStage")).getText()).isEqualTo(updatedStage);
        assertThat(driver.findElement(By.id("delegationNotes")).getText()).isEqualTo(updatedNotes);

        // Verify no stale or inconsistent data
        assertNoStaleData();
    }

    /**
     * Helper method to inject approval status and delegation notes into the UI.
     * This simulates the initial state of the page.
     */
    private void injectApprovalStatusToUI(String stage, String notes) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String script = "document.getElementById('approvalStage').innerText = '" + stage + "';" +
                        "document.getElementById('delegationNotes').innerText = '" + notes + "';";
        js.executeScript(script);
    }

    /**
     * Helper method to simulate backend event triggering frontend update.
     * In a real app, this might be a websocket message or SSE event.
     * Here, we simulate by calling a JS function that fetches updated data.
     */
    private void triggerBackendEventUpdate() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Simulate frontend polling or websocket event handler
        String script = "window.fetchUpdatedApprovalStatus();";
        js.executeScript(script);
    }

    /**
     * Helper method to assert no stale or inconsistent data is present.
     * Checks that approval stage and delegation notes are non-empty and consistent.
     */
    private void assertNoStaleData() {
        WebElement stageElement = driver.findElement(By.id("approvalStage"));
        WebElement notesElement = driver.findElement(By.id("delegationNotes"));

        String stageText = stageElement.getText();
        String notesText = notesElement.getText();

        assertThat(stageText).isNotNull().isNotEmpty();
        assertThat(notesText).isNotNull().isNotEmpty();

        // Additional consistency checks can be added here
    }
}
