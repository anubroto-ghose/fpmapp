/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-41
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:47:27
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TravelRequestApprovalTest {

    private WebDriver driver;

    @Mock
    private FpmTravelController fpmTravelController;

    @InjectMocks
    private TravelRequestApprovalTest travelRequestApprovalTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/travel-requests");
    }

    @Test
    public void testApproveTravelRequest() {
        // Mocking the service response
        when(fpmTravelController.getPendingTravelRequests()).thenReturn(getMockPendingTravelRequests());

        // Step 1: Navigate to the travel request approval section
        WebElement travelRequestSection = driver.findElement(By.id("travelRequestSection"));
        travelRequestSection.click();

        // Step 2: Select a travel request pending approval
        WebElement pendingRequest = driver.findElement(By.xpath("//div[@class='request' and @data-status='Pending'][1]"));
        pendingRequest.click();

        // Step 3: Click on the 'Approve' button
        WebElement approveButton = driver.findElement(By.id("approveButton"));
        approveButton.click();

        // Assertions
        WebElement statusMessage = driver.findElement(By.id("statusMessage"));
        assertEquals("Approved", statusMessage.getText());

        // Verify notification sent to requester
        verify(fpmTravelController).sendApprovalNotification(anyInt());
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    private List<TravelRequest> getMockPendingTravelRequests() {
        List<TravelRequest> requests = new ArrayList<>();
        requests.add(new TravelRequest(1, "Travel to New York", "Pending"));
        return requests;
    }
}