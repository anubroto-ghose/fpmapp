# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4655
# Epic: BANK-4644
# Generated on: 2025-07-30 17:03:06
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: FPM API Tests

  Scenario: Verify GET /getfpmlist/empid/{empId} returns 404 for invalid empId
    Given the API is updated to return the new approval status
    When I send a GET request to "/getfpmlist/empid/99999"
    Then the response status should be 404
    And the response body should indicate that the resource was not found