/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4655
 * Epic: BANK-4644
 * Generated on: 2025-07-30 17:03:06
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class FpmApiTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        // Setup code if needed
    }

    @Test
    public void testGetFpmlistWithInvalidEmpId() throws Exception {
        String invalidEmpId = "99999"; // Example of an invalid empId

        mockMvc.perform(get("/getfpmlist/empid/" + invalidEmpId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}