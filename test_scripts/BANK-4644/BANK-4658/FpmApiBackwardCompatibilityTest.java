/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4658
 * Epic: BANK-4644
 * Generated on: 2025-07-30 17:02:15
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class FpmApiBackwardCompatibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FpmCommonController fpmCommonController;

    @InjectMocks
    private FpmDealsheetController fpmDealsheetController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFpmListBackwardCompatibility() throws Exception {
        String empId = "12345";
        String expectedResponse = "{\"approval_status\":\"DRAFT\", \"data\":[]}"; // Example of old response format

        // Mocking the service response
        when(fpmCommonController.getFpmList(empId)).thenReturn(expectedResponse);

        // Sending GET request to the API
        mockMvc.perform(get("/getfpmlist/empid/" + empId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expectedResponse));
    }
}