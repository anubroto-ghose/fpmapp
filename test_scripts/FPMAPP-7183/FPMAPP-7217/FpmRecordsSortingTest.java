/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7217
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:10:48
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FpmRecordsSortingTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/fpm-records"); // Adjust URL accordingly
        loginAsPortfolioAnalyst(); // Method to log in as the analyst
    }

    private void loginAsPortfolioAnalyst() {
        // Implement login logic to authenticate user
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameField.sendKeys("analyst_user");
        passwordField.sendKeys("analyst_password");
        loginButton.click();
    }

    @Test
    public void testSortingByCustomer() {
        WebElement sortByCustomerButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("sortByCustomer")));
        sortByCustomerButton.click(); // Click on the sorting option beside customer

        // Wait for the table to refresh
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recordTable"))); // Wait until table loads

        List<WebElement> rows = driver.findElements(By.cssSelector("#recordTable tbody tr"));
        String previousCustomer = "";
        for (WebElement row : rows) {
            String currentCustomer = row.findElement(By.cssSelector("td.customer")) // Adjust selector accordingly
                                      .getText();
            if (!previousCustomer.isEmpty()) {
                assertTrue(previousCustomer.compareTo(currentCustomer) <= 0, "Records are not sorted correctly by customer.");
            }
            previousCustomer = currentCustomer;
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}