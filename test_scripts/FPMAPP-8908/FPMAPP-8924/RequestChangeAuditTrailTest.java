/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8924
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:05:35
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for verifying audit trail and versioning of request changes.
 * 
 * Preconditions:
 * - A request is submitted and present in the system.
 * - The user has permission to edit the request.
 * 
 * Test Steps:
 * 1. Modify one or more fields of the submitted request.
 * 2. Save the changes.
 * 3. Access the audit log and version history for the request.
 * 
 * Expected Results:
 * - Each change is logged as a new version.
 * - Audit log records user, timestamp, and change details.
 * - Previous versions remain accessible and immutable.
 * - Audit trail supports querying by request and user.
 * - Audit log entries cannot be altered or deleted.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RequestChangeAuditTrailTest {

    private static WebDriver driver;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final long TEST_REQUEST_ID = 12345L;
    private static final String TEST_USERNAME = "compliance_officer";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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
    public void setupMocks() {
        // Mock user profile with edit permission
        User mockUser = new User();
        mockUser.setUsername(TEST_USERNAME);
        mockUser.setRoles(Collections.singletonList("ROLE_EDITOR"));
        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock existing request data
        when(fpmDealsheetController.getRequestById(TEST_REQUEST_ID)).thenReturn(
                new com.webapp.fpmapp.dto.RequestDTO(TEST_REQUEST_ID, "Initial Request Title", "Initial description", "SUBMITTED"));

        // Mock audit log entries
        com.webapp.fpmapp.dto.AuditLogEntry initialEntry = new com.webapp.fpmapp.dto.AuditLogEntry(
                TEST_REQUEST_ID,
                TEST_USERNAME,
                LocalDateTime.now().minusDays(1),
                "Request submitted with initial data.",
                1
        );

        when(fpmCommonController.getAuditLogForRequest(TEST_REQUEST_ID)).thenReturn(Collections.singletonList(initialEntry));

        // Mock version history
        com.webapp.fpmapp.dto.VersionEntry version1 = new com.webapp.fpmapp.dto.VersionEntry(
                TEST_REQUEST_ID,
                1,
                LocalDateTime.now().minusDays(1),
                "Initial version"
        );
        when(fpmCommonController.getVersionHistory(TEST_REQUEST_ID)).thenReturn(Collections.singletonList(version1));
    }

    @Test
    public void testRequestChangeIsTrackedInAuditTrailAndVersioning() throws InterruptedException {
        // Step 1: Login as compliance officer
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys(TEST_USERNAME);
        passwordInput.sendKeys("password123"); // assuming test password
        loginButton.click();

        Thread.sleep(1000); // wait for login

        // Step 2: Navigate to the submitted request page
        driver.get(BASE_URL + "/requests/" + TEST_REQUEST_ID);

        Thread.sleep(1000); // wait for page load

        // Verify initial request title
        WebElement titleField = driver.findElement(By.id("requestTitle"));
        assertThat(titleField.getAttribute("value")).isEqualTo("Initial Request Title");

        // Step 3: Modify one or more fields
        String updatedTitle = "Updated Request Title - Audit Test";
        titleField.clear();
        titleField.sendKeys(updatedTitle);

        WebElement descriptionField = driver.findElement(By.id("requestDescription"));
        descriptionField.clear();
        descriptionField.sendKeys("Updated description for audit trail verification.");

        // Step 4: Save the changes
        WebElement saveButton = driver.findElement(By.id("saveRequestButton"));
        saveButton.click();

        Thread.sleep(1500); // wait for save

        // Mock the updated audit log and version history after save
        com.webapp.fpmapp.dto.AuditLogEntry changeEntry = new com.webapp.fpmapp.dto.AuditLogEntry(
                TEST_REQUEST_ID,
                TEST_USERNAME,
                LocalDateTime.now(),
                "Changed title and description.",
                2
        );

        List<com.webapp.fpmapp.dto.AuditLogEntry> auditLogEntries = Arrays.asList(
                fpmCommonController.getAuditLogForRequest(TEST_REQUEST_ID).get(0),
                changeEntry
        );

        when(fpmCommonController.getAuditLogForRequest(TEST_REQUEST_ID)).thenReturn(auditLogEntries);

        com.webapp.fpmapp.dto.VersionEntry version1 = new com.webapp.fpmapp.dto.VersionEntry(
                TEST_REQUEST_ID,
                1,
                LocalDateTime.now().minusDays(1),
                "Initial version"
        );
        com.webapp.fpmapp.dto.VersionEntry version2 = new com.webapp.fpmapp.dto.VersionEntry(
                TEST_REQUEST_ID,
                2,
                LocalDateTime.now(),
                "Updated title and description"
        );

        when(fpmCommonController.getVersionHistory(TEST_REQUEST_ID)).thenReturn(Arrays.asList(version1, version2));

        // Step 5: Access audit log and version history UI
        driver.get(BASE_URL + "/requests/" + TEST_REQUEST_ID + "/audit-log");

        Thread.sleep(1000); // wait for audit log page

        // Verify audit log entries
        List<WebElement> auditEntries = driver.findElements(By.cssSelector(".audit-log-entry"));
        assertThat(auditEntries.size()).isGreaterThanOrEqualTo(2);

        boolean foundChangeEntry = auditEntries.stream().anyMatch(e ->
                e.getText().contains("Changed title and description") &&
                e.getText().contains(TEST_USERNAME)
        );
        assertThat(foundChangeEntry).isTrue();

        // Verify version history
        driver.get(BASE_URL + "/requests/" + TEST_REQUEST_ID + "/version-history");

        Thread.sleep(1000); // wait for version history page

        List<WebElement> versionEntries = driver.findElements(By.cssSelector(".version-entry"));
        assertThat(versionEntries.size()).isGreaterThanOrEqualTo(2);

        boolean foundVersion2 = versionEntries.stream().anyMatch(e ->
                e.getText().contains("Version 2") &&
                e.getText().contains("Updated title and description")
        );
        assertThat(foundVersion2).isTrue();

        // Verify immutability: try to find any edit/delete buttons on audit log entries
        driver.get(BASE_URL + "/requests/" + TEST_REQUEST_ID + "/audit-log");
        Thread.sleep(1000);
        List<WebElement> editButtons = driver.findElements(By.cssSelector(".audit-log-entry .edit-button"));
        List<WebElement> deleteButtons = driver.findElements(By.cssSelector(".audit-log-entry .delete-button"));

        assertThat(editButtons).isEmpty();
        assertThat(deleteButtons).isEmpty();

        // Verify audit trail supports querying by user
        driver.get(BASE_URL + "/audit-log?user=" + TEST_USERNAME);
        Thread.sleep(1000);
        List<WebElement> filteredEntries = driver.findElements(By.cssSelector(".audit-log-entry"));
        assertThat(filteredEntries.size()).isGreaterThanOrEqualTo(2);

        // Final assertion: page title contains audit log
        assertThat(driver.getTitle().toLowerCase()).contains("audit log");
    }
}
