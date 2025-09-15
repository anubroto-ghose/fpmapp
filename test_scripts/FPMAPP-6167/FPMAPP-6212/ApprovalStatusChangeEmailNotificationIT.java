/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6212
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:46:57
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.SocketUtils;

import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test verifying that when approval status changes,
 * an automatic email notification is sent using configured SMTP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=2525",
        "spring.mail.username=testuser",
        "spring.mail.password=testpass"
})
public class ApprovalStatusChangeEmailNotificationIT {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @MockBean
    private JavaMailSender mailSender;

    private List<SimpleMailMessage> sentMessages;

    @BeforeAll
    public void setUpAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);

        sentMessages = new ArrayList<>();

        Mockito.doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            if (args.length > 0 && args[0] instanceof SimpleMailMessage) {
                sentMessages.add((SimpleMailMessage) args[0]);
            }
            return null;
        }).when(mailSender).send(Mockito.any(SimpleMailMessage.class));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        Mockito.reset(mailSender);
        sentMessages.clear();
    }

    @Test
    @DisplayName("Verify email notification is sent upon approval status change")
    public void testEmailNotificationOnApprovalStatusChange() throws InterruptedException {
        // Preconditions: User involved in approval workflow - simulate login and approval request
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Navigate to the FpmDealsheetController's UI page (e.g., approval request list)
        String baseUrl = "http://localhost:" + port + "/dealsheets";
        driver.get(baseUrl);

        // Simulate login - replace with actual selectors and login steps
        driver.findElement(By.id("username")).sendKeys("test_approver");
        driver.findElement(By.id("password")).sendKeys("Password123!");
        driver.findElement(By.id("login-button")).click();

        // Wait for redirection after login
        Thread.sleep(1000);

        // Select a deal sheet requiring approval - we assume a button/link triggers the approval status change
        driver.findElement(By.cssSelector("button.approve-dealsheet")).click();

        // Wait for backend processing
        Thread.sleep(2000);

        // VERIFY: Email notification should be sent via SMTP (mocked JavaMailSender)

        // Wait up to 5s for async email sending (simulated delay)
        int tries = 0;
        while (sentMessages.isEmpty() && tries < 5) {
            Thread.sleep(1000);
            tries++;
        }

        assertThat(sentMessages).isNotEmpty();

        SimpleMailMessage mailMessage = sentMessages.get(0);

        // Assert recipient - assuming approver's email
        assertThat(mailMessage.getTo()).isNotNull();
        assertThat(mailMessage.getTo()).contains("approver@example.com");

        // Assert subject and content contain correct approval status info
        assertThat(mailMessage.getSubject()).containsIgnoringCase("Approval Status Changed");
        assertThat(mailMessage.getText()).containsIgnoringCase("approved");

        // Assert no error logged to system
        // (MockBean won't produce logs, but in real test log capture/assertion needed)
    }
}