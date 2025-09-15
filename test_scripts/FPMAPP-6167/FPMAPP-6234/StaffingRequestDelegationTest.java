/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6234
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:30:23
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full Spring Boot integration test with embedded WebDriver (ChromeDriver) to validate delegation allowed only for permitted roles and auditing.
 * 
 * Tests POST /staffing/request/approval with delegation action.
 * 
 * Scenario covers valid delegation from Director role to Manager role,
 * confirms DB audit log creation, approval status update,
 * and rejection of delegation from non-permitted roles.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // assume default port 8080
public class StaffingRequestDelegationTest {

    private static WebDriver driver;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    // Dummy DB/access mocks - in real tests, use TestContainers or embedded DB and repository mocks
    // Simulate backend responses for approval status and audit logs

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() throws Exception {
        // Mock user profile service to return Director role for user "directorUser"
        Mockito.when(fpmUserProfileController.getUserRoleByUserId("directorUser"))
                .thenReturn("Director");

        // Mock delegation allowed config (only Directors allowed to delegate)
        Mockito.when(fpmCommonController.isRoleAllowedToDelegate("Director"))
                .thenReturn(true);
        Mockito.when(fpmCommonController.isRoleAllowedToDelegate("Manager"))
                .thenReturn(false);

        // Mock audit log creation: returning an auditLogId
        Mockito.when(fpmCommonController.logApprovalAudit(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("auditlog-12345");

        // Mock staffing request approval current status
        Mockito.when(fpmCommonController.getCurrentApproverRoleForStaffingRequest(1001L))
                .thenReturn("Director");
    }

    /**
     * Test delegation success from Director role to Manager role
     */
    @Test
    public void testDelegationSuccessfulForPermittedRole() throws Exception {
        long staffingRequestId = 1001L;

        // Prepare JSON payload for delegation
        String jsonPayload = String.format("{" +
                \"action\": \"delegate\"," +
                \"roleId\": \"Director\"," +
                \"delegatedToRoleId\": \"Manager\" }"
        );

        // Perform POST via MockMvc to mock backend
        RequestBuilder request = MockMvcRequestBuilders
                .post("/staffing/request/approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .header("X-User-Id", "directorUser")
                .param("requestId", String.valueOf(staffingRequestId));

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();

        // Assert response includes updated approvalStatus, currentApproverRole, and auditLogId
        assertThat(content).contains("delegated");
        assertThat(content).contains("Manager");
        assertThat(content).contains("auditlog-12345");

        // Validate backend mocks - audit log must be called
        Mockito.verify(fpmCommonController)
                .logApprovalAudit(eq(String.valueOf(staffingRequestId)), eq("directorUser"), eq("delegate"), Mockito.anyString());

        // Additionally test via Selenium that delegation UI workflow is functional (simulate sending request)
        driver.get(BASE_URL + "/staffing/delegation-ui");

        // Wait for page to load and input fields
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement actionSelect = wait.until(d -> d.findElement(By.id("actionSelect")));
        actionSelect.sendKeys("delegate");

        WebElement roleIdInput = driver.findElement(By.id("roleIdInput"));
        roleIdInput.clear();
        roleIdInput.sendKeys("Director");

        WebElement delegatedToRoleIdInput = driver.findElement(By.id("delegatedToRoleIdInput"));
        delegatedToRoleIdInput.clear();
        delegatedToRoleIdInput.sendKeys("Manager");

        WebElement requestIdInput = driver.findElement(By.id("requestIdInput"));
        requestIdInput.clear();
        requestIdInput.sendKeys(String.valueOf(staffingRequestId));

        WebElement submitButton = driver.findElement(By.id("submitDelegationBtn"));
        submitButton.click();

        // Wait for ajax response / result element
        WebElement resultOutput = wait.until(d -> d.findElement(By.id("delegationResult")));

        String resultText = resultOutput.getText();

        assertThat(resultText).contains("successfully delegated");
        assertThat(resultText).contains("Manager");

    }

    /**
     * Test delegation rejected when user role is not permitted
     */
    @Test
    public void testDelegationRejectedForNonPermittedRole() throws Exception {
        long staffingRequestId = 1002L;

        // Mock user profile service to return Manager (not permitted to delegate)
        Mockito.when(fpmUserProfileController.getUserRoleByUserId("managerUser"))
                .thenReturn("Manager");

        // Prepare JSON payload for delegation attempt by Manager
        String jsonPayload = String.format("{" +
                \"action\": \"delegate\"," +
                \"roleId\": \"Manager\"," +
                \"delegatedToRoleId\": \"TeamLead\" }"
        );

        // Perform POST via MockMvc
        RequestBuilder request = MockMvcRequestBuilders
                .post("/staffing/request/approval")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .header("X-User-Id", "managerUser")
                .param("requestId", String.valueOf(staffingRequestId));

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();

        assertThat(content).contains("Delegation not permitted for role");

        // Verify audit log NOT called
        Mockito.verify(fpmCommonController, Mockito.never())
                .logApprovalAudit(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }
}