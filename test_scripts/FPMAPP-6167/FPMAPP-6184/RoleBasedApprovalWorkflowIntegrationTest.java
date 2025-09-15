/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6184
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:08:25
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.controllers.FpmTravelController;
import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RoleBasedApprovalWorkflowIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConversionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setUpClass() {
        // Use headless mode for CI compatibility
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profiles for manager and director roles
        User managerUser = new User();
        managerUser.setId(1L);
        managerUser.setUsername("manager.user");
        managerUser.setRole("MANAGER");

        User directorUser = new User();
        directorUser.setId(2L);
        directorUser.setUsername("director.user");
        directorUser.setRole("DIRECTOR");

        Mockito.when(fpmUserProfileController.getUserByUsername("manager.user")).thenReturn(managerUser);
        Mockito.when(fpmUserProfileController.getUserByUsername("director.user")).thenReturn(directorUser);

        // Mock dealsheet approvals
        Mockito.when(fpmDealsheetController.submitDealSheet(Mockito.any())).thenAnswer(invocation -> {
            // Extract request payload
            var request = invocation.getArgument(0, com.webapp.fpmapp.dto.DealSheetRequest.class);
            // Based on requested amount route to director for above-manager threshold
            if (request.getAmount() > 50000) { // Manager threshold assumed 50000
                return new com.webapp.fpmapp.dto.ApprovalResponse("DIRECTOR", "PENDING_APPROVAL", "Routed to Director");
            } else {
                return new com.webapp.fpmapp.dto.ApprovalResponse("MANAGER", "PENDING_APPROVAL", "Routed to Manager");
            }
        });

        // Mock staffing request approvals
        Mockito.when(fpmCommonController.submitStaffingRequest(Mockito.any())).thenAnswer(invocation -> {
            var staffingRequest = invocation.getArgument(0, com.webapp.fpmapp.dto.StaffingRequest.class);
            // Threshold for manager is $20_000
            if (staffingRequest.getCost() > 20000) {
                return new com.webapp.fpmapp.dto.ApprovalResponse("DIRECTOR", "PENDING_APPROVAL", "Routed to Director");
            } else {
                return new com.webapp.fpmapp.dto.ApprovalResponse("MANAGER", "PENDING_APPROVAL", "Routed to Manager");
            }
        });

        // Mock travel request approvals
        Mockito.when(fpmTravelController.submitTravelRequest(Mockito.any())).thenAnswer(invocation -> {
            var travelRequest = invocation.getArgument(0, com.webapp.fpmapp.dto.TravelRequest.class);
            if (travelRequest.getTravelCost() > 10000) {
                return new com.webapp.fpmapp.dto.ApprovalResponse("DIRECTOR", "PENDING_APPROVAL", "Routed to Director");
            } else {
                return new com.webapp.fpmapp.dto.ApprovalResponse("MANAGER", "PENDING_APPROVAL", "Routed to Manager");
            }
        });
    }

    @Test
    public void testDealSheetApprovalRouting() {
        driver.get("http://localhost:" + port + "/dealsheet/submit");

        WebElement amountInput = driver.findElement(By.id("amount"));
        WebElement submitBtn = driver.findElement(By.id("submit-dealsheet"));

        // Step 1: Submit deal sheet requiring approval above manager's threshold (> 50000)
        amountInput.clear();
        amountInput.sendKeys("75000");
        submitBtn.click();

        // Verify routed to Director
        WebElement outcomeMessage = driver.findElement(By.id("approval-routing-result"));
        String text = outcomeMessage.getText();
        assertTrue(text.contains("Director"), "Deal sheet approval should route to Director.");
    }

    @Test
    public void testStaffingRequestApprovalRouting() {
        driver.get("http://localhost:" + port + "/staffing/submit");

        WebElement costInput = driver.findElement(By.id("cost"));
        WebElement submitBtn = driver.findElement(By.id("submit-staffing"));

        // Step 2: Submit staffing request that meets manager threshold (<= 20000)
        costInput.clear();
        costInput.sendKeys("15000");
        submitBtn.click();

        // Verify routed to Manager
        WebElement outcomeMessage = driver.findElement(By.id("approval-routing-result"));
        String text = outcomeMessage.getText();
        assertTrue(text.contains("Manager"), "Staffing request should route to Manager.");
    }

    @Test
    public void testTravelRequestApprovalRouting() {
        driver.get("http://localhost:" + port + "/travel/submit");

        WebElement travelCostInput = driver.findElement(By.id("travelCost"));
        WebElement submitBtn = driver.findElement(By.id("submit-travel"));

        // Step 3: Travel request routing based on thresholds

        // Case 1: High cost (> 10000) should go to Director
        travelCostInput.clear();
        travelCostInput.sendKeys("12000");
        submitBtn.click();
        WebElement outcomeMessage = driver.findElement(By.id("approval-routing-result"));
        String text = outcomeMessage.getText();
        assertTrue(text.contains("Director"), "Travel request above threshold should route to Director.");

        // Case 2: Low cost (<= 10000) should go to Manager
        driver.get("http://localhost:" + port + "/travel/submit");
        travelCostInput = driver.findElement(By.id("travelCost"));
        submitBtn = driver.findElement(By.id("submit-travel"));
        travelCostInput.clear();
        travelCostInput.sendKeys("5000");
        submitBtn.click();
        outcomeMessage = driver.findElement(By.id("approval-routing-result"));
        text = outcomeMessage.getText();
        assertTrue(text.contains("Manager"), "Travel request below threshold should route to Manager.");
    }
}