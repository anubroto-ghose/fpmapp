# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5258
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:54:16
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Admin override logging for currency rates
  
  As a system administrator,
  I want to see audit log entries for currency admin overrides,
  So that I can verify that all override actions are properly recorded with full details.

  Background:
    Given I am logged in as an administrator with username "adminUser123"
    And I have successfully overridden the currency rate of "USD" from 1.1200 to 1.1500

  Scenario: Verify recent currency override log entry is displayed with correct details
    When I navigate to the admin logs page
    And I search for override logs with currency code "USD"
    Then the override logs should include an entry with:
      | Admin ID     | adminUser123 |
      | Currency Code| USD          |
      | Old Rate    | 1.1200        |
      | New Rate    | 1.1500        |
      | A valid timestamp                       |

  
  # Step Definitions would be implemented in Java to bind below Gherkin steps to interaction with UI and mocks:
  # Given I am logged in as an administrator with username "adminUser123"
  # And I have successfully overridden the currency rate of "USD" from 1.1200 to 1.1500
  # When I navigate to the admin logs page
  # And I search for override logs with currency code "USD"
  # Then the override logs should include an entry with:
  #   | Admin ID     | adminUser123 |
  #   | Currency Code| USD          |
  #   | Old Rate    | 1.1200        |
  #   | New Rate    | 1.1500        |
  #   | A valid timestamp                       |
