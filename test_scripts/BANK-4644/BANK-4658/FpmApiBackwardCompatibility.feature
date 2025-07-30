# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-4658
# Epic: BANK-4644
# Generated on: 2025-07-30 17:02:15
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: FPM API Backward Compatibility

  Scenario: Ensure backward compatibility for existing clients
    Given the API is updated
    When I send a GET request to "/getfpmlist/empid/{empId}" using an old client version
    Then the response should comply with the previous API format
    And the response should include the necessary old approval status
