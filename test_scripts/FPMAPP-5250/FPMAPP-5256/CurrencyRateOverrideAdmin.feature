# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5256
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:20:52
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin Override Currency Rate
  As a system administrator
  I want to override currency rates
  So that I can update rates manually and notify the system

  Background:
    Given I am logged in as an admin user

  Scenario: Successful currency rate override by admin
    When I navigate to the currency rate management section
    And I select the currency "USD"
    And I enter the new rate "1.25"
    And I submit the changes
    Then the currency rate for "USD" should be updated successfully
    And I should see an alert confirming the override action
    And the change should be logged in the system
