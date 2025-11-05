# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7222
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:08:49
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Paginated Listing of FPM Records

  Scenario: Verify the number of records displayed in the paginated FPM listing
    Given I am a logged-in user
    And I navigate to the FPM records page
    When I check the total number of records displayed on the page
    Then the number of records displayed should equal the defined number of records per page

  Background:
    Given the following FPM records exist:
      | Record ID | Status | Practice |
      | 1         | Active | Finance  |
      | 2         | Active | Operations|
      | 3         | Active | HR       |
      | 4         | Active | Sales    |
      | 5         | Active | IT       |
      | 6         | Active | Marketing |
      | 7         | Active | Support   |
      | 8         | Active | R&D      |
      | 9         | Active | Legal    |
      | 10        | Active | Strategy  |
    And the pagination settings are set to display 10 records per page.
