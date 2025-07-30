/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4657
 * Epic: BANK-4644
 * Generated on: 2025-07-30 17:02:31
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class FpmApiTest {

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
    public void testGetFpmListReturnsApprovalStatus() throws Exception {
        // Arrange
        String empId = "12345";
        String expectedApprovalStatus = "Pending Approval";
        when(fpmCommonController.getFpmList(any())).thenReturn(new FpmListResponse(empId, expectedApprovalStatus));

        // Act & Assert
        mockMvc.perform(get("/getfpmlist/empid/" + empId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.approvalStatus").value(expectedApprovalStatus));
    }
}
