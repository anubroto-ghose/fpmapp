/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4654
 * Epic: BANK-4644
 * Generated on: 2025-07-30 17:03:25
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class FpmApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FpmForecastController fpmForecastController;

    @InjectMocks
    private FpmDealsheetController fpmDealsheetController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFpmListWithApprovalStatus() throws Exception {
        // Given
        String empId = "12345";
        String expectedResponse = "[{\"fpmId\":1, \"approvalStatus\":\"Pending Approval\"}, {\"fpmId\":2, \"approvalStatus\":\"Approved\"}]";
        when(fpmForecastController.getFpmList(empId)).thenReturn(expectedResponse);

        // When
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/getfpmlist/empid/" + empId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(content().json(expectedResponse));
    }
}