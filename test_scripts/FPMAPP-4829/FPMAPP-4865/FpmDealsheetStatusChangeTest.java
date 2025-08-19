/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4865
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-19 06:13:47
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FpmDealsheetStatusChangeTest {

    private WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/admin");
    }

    @Test
    public void testStatusChangeNotification() throws InterruptedException {
        // Log in as admin
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("adminpass");
        loginButton.click();

        // Wait for the page to load
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statusChangeSection")));

        // Change the status of the request
        WebElement statusDropdown = driver.findElement(By.id("statusDropdown"));
        statusDropdown.click();
        WebElement newStatus = driver.findElement(By.xpath("//option[text()='Approved']"));
        newStatus.click();
        WebElement changeStatusButton = driver.findElement(By.id("changeStatusButton"));
        changeStatusButton.click();

        // Wait for notification to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationCenter")));

        // Check for the notification
        WebElement notification = driver.findElement(By.id("notificationCenter"));
        assertTrue(notification.getText().contains("Your request status has been changed to Approved"), "Notification not received");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}