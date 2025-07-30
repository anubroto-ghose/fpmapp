# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4654
# Epic: BANK-4644
# Generated on: 2025-07-30 17:03:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: FPM API Approval Status

  Scenario: Verify GET /getfpmlist/empid/{empId} returns new approval status
    Given the API is updated to return the new approval status
    When I send a GET request to "/getfpmlist/empid/12345"
    Then the response should include the new approval status for each FPM
    And the response status should be 200 OK
    And the response should be in JSON format
    And the response should contain the approval status for each FPM
