# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4657
# Epic: BANK-4644
# Generated on: 2025-07-30 17:02:31
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Get FPM List

  Scenario: Verify GET /getfpmlist/empid/{empId} returns new approval status
    Given the API is modified to include the new approval status in the response
    When I send a GET request to "/getfpmlist/empid/12345"
    Then the response should include the new approval status for the specified employee
    And the approval status should be "Pending Approval"
