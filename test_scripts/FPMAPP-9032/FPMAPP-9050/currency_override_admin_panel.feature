# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-9050
# Epic: FPMAPP-9032
# Generated on: 2026-03-31 16:50:30
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Override Admin Panel
  As a finance admin
  I want to view current and historical currency rates
  So that I can manage currency rate overrides accurately

  Background:
    Given I am logged in as a finance admin with appropriate permissions
    And the Currency Override Admin Panel is accessible
    And the backend service has current and historical currency rate data available

  Scenario: View current currency rates successfully
    When I navigate to the Currency Override Admin Panel
    Then I should see the current currency rates displayed accurately
    And there should be no errors or missing data in the UI

  Scenario: View historical currency rates successfully
    Given I am on the Currency Override Admin Panel
    When I access the historical rates section
    Then I should see historical currency rates displayed with correct dates and values
    And there should be no errors or missing data in the UI

  Scenario: Data matches backend service responses
    When I navigate to the Currency Override Admin Panel
    And I access the historical rates section
    Then the displayed current currency rates should match the backend service responses
    And the displayed historical currency rates should match the backend service responses
    And no errors should be present in the UI
