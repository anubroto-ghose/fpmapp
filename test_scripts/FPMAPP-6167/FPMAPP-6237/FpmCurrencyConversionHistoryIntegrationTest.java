/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6237
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:28:22
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.controller.FpmTravelController;
import com.webapp.fpmapp.controller.FpmUserProfileController;
import com.webapp.fpmapp.dto.CurrencyConversionEntryDTO;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.service.CurrencyConvertionController;
import com.webapp.fpmapp.service.FpmCommonController;
import com.webapp.fpmapp.service.FpmForecastController;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FpmCurrencyConversionHistoryIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmTravelController travelController;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testINRtoJPYConversionEntryDisplayedCorrectly() {
        // Arrange
        // Mock the logged in user has the correct role to access the approval features
        when(userProfileController.hasRole(any(String.class))).thenReturn(true);

        // Setup a mock conversion entry for INR to JPY
        CurrencyConversionEntryDTO mockEntry = new CurrencyConversionEntryDTO();
        mockEntry.setTransactionDate(LocalDate.of(2025, 9, 1));
        mockEntry.setCurrencyFrom("INR");
        mockEntry.setCurrencyTo("JPY");
        mockEntry.setAmountFrom(10000.50);
        mockEntry.setAmountTo(1650000.75); // Example amount after conversion
        mockEntry.setStatus("Approved");
        mockEntry.setAuditReference("AuditID-123456");

        when(currencyConvertionController.getConversionHistory("")).thenReturn(java.util.List.of(mockEntry));

        // Act
        String baseUrl = "http://localhost:" + port;
        driver.get(baseUrl + "/transaction-history");

        // Wait until page table with data is visible
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("conversion-history-table")));

        // Find the INR to JPY row
        WebElement row = table.findElement(By.xpath(".//tr[td[contains(text(), 'INR')] and td[contains(text(), 'JPY')]]"));
        
        // Extract columns
        WebElement dateCell = row.findElement(By.xpath("./td[1]"));
        WebElement amountFromCell = row.findElement(By.xpath("./td[2]"));
        WebElement amountToCell = row.findElement(By.xpath("./td[3]"));
        WebElement statusCell = row.findElement(By.xpath("./td[4]"));
        WebElement auditRefCell = row.findElement(By.xpath("./td[5]"));

        // Assert

        // Date must be formatted DD-MM-YYYY
        String dateText = dateCell.getText().trim();
        DateTimeFormatter uiFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String expectedDate = mockEntry.getTransactionDate().format(uiFormatter);
        assertThat("Date format and value", dateText, equalTo(expectedDate));

        // Amount in INR with correct currency symbol and formatted
        String amountFromText = amountFromCell.getText().trim();
        // Assuming INR symbol ₹ used and grouping for large numbers, e.g. ₹10,000.50
        String expectedAmountFrom = String.format("₹%,.2f", mockEntry.getAmountFrom());
        assertThat("INR amount formatting", amountFromText, equalTo(expectedAmountFrom));

        // Amount in JPY with currency symbol and correct amount
        String amountToText = amountToCell.getText().trim();
        String expectedAmountTo = String.format("¥%,.2f", mockEntry.getAmountTo());
        assertThat("JPY amount formatting", amountToText, equalTo(expectedAmountTo));

        // Status is correctly displayed
        String statusText = statusCell.getText().trim();
        assertThat("Status display", statusText, equalTo(mockEntry.getStatus()));

        // Audit trail reference should be displayed and non-empty
        String auditRefText = auditRefCell.getText().trim();
        assertThat("Audit trail reference present", auditRefText, not(isEmptyOrNullString()));
        assertThat("Audit reference value", auditRefText, equalTo(mockEntry.getAuditReference()));
    }
}
