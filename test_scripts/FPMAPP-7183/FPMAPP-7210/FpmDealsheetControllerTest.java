/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7210
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:13:13
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.WebApplicationContext;

import io.restassured.RestAssured;

@SpringBootTest
@AutoConfigureMockMvc
public class FpmDealsheetControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.standaloneSetup(webApplicationContext);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGetFPMListWithValidToken() {
        String validJwtToken = "Bearer your_valid_jwt_token_here"; // replace with actual token generation logic

        given()
            .auth().oauth2(validJwtToken)
            .when()
            .get("/api/fpm/list?page=0&pageSize=10&status=active&practice=examplePractice&sort=projectName")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(containsString("FPM Records"));
    }

    @Test
    public void testGetFPMListWithInvalidToken() {
        String invalidJwtToken = "Bearer invalid_token";

        given()
            .auth().oauth2(invalidJwtToken)
            .when()
            .get("/api/fpm/list")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}