# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7221
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:09:16
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Dynamic UI update upon filtering FPM records

  Scenario: User filters FPM records by status
    Given the user is logged in
    And the user is on the FPM records page
    When the user selects a filter option from the status dropdown
    Then the FPM records should dynamically update to show only those matching the selected filter without refreshing the page

  Scenario: User filters FPM records by practice
    Given the user is logged in
    And the user is on the FPM records page
    When the user selects a filter option from the practice dropdown
    Then the FPM records should dynamically update to show only those matching the selected filter without refreshing the page