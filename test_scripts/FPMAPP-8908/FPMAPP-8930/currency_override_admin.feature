# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8930
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 14:59:40
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin overrides currency exchange rates with audit logging and alerting
  
  As a financial analyst with admin privileges
  I want to override currency exchange rates in the system
  So that the system reflects the updated rates immediately with proper audit and alerting

  Background:
    Given the system has currency exchange rates available
    And I am logged in as an admin user

  Scenario: Successfully override a currency exchange rate
    When I navigate to the currency override page
    And I select the currency "EUR"
    And I enter a new rate "0.90"
    And I submit the override
    Then the override should be saved and reflected immediately
    And an audit log entry should be created with the override details
    And an alert notification should be triggered for the override event

  Scenario: Attempt to override with invalid negative rate
    When I navigate to the currency override page
    And I select the currency "USD"
    And I enter an invalid rate "-1.00"
    And I submit the override
    Then the system should reject the override
    And an error message "Invalid rate: must be positive" should be displayed
    And no audit log entry should be created
    And no alert notification should be triggered
