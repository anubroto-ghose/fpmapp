/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7216
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:11:08
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FpmDealsheetSortingTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        // Setup Mock behavior, this is just an example, adjust as needed
        doReturn(getSortedMockResponse()).when(service).getFpmRecords(Mockito.any());
    }

    @Test
    public void testSortByProjectName() throws Exception {
        // Perform the request to sort by project name
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/fpm/list")
                .param("sort", "projectName")
                .accept(MediaType.APPLICATION_JSON));

        // Verify the records are sorted by project name in ascending order
        resultActions.andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].projectName").value("A Project"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.records[1].projectName").value("B Project"));
    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(service);
    }

    private List<FpmRecord> getSortedMockResponse() {
        List<FpmRecord> records = new ArrayList<>();
        records.add(new FpmRecord("A Project", "Customer1", LocalDate.now()));
        records.add(new FpmRecord("B Project", "Customer2", LocalDate.now()));
        return records;
    }
}