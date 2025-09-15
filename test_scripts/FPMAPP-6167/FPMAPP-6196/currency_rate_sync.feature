# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6196
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:58:42
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time Currency Rate Synchronization
  
  As a financial admin
  I want to trigger currency rate synchronization
  So that the system updates all currencies with the latest exchange rates

  Background: 
    Given the backend currency rate service is running
    And the third-party currency rate service is available
    And I am an authenticated admin user

  Scenario: Successful real-time currency rate synchronization via API
    When I send a POST request to "/currency/rates/sync"
    Then the response status should be 200 OK
    And the response body should have "success" equal to true
    And the response body should contain a valid "syncedAt" timestamp
    
  Scenario: Database updated after currency rate synchronization
    Given the last synchronization timestamp is recorded
    When I retrieve the latest sync timestamp from the CurrencyRates table
    Then the latest sync timestamp should match the "syncedAt" value from the sync response

  Scenario: No errors occur during synchronization
    When I send a POST request to "/currency/rates/sync"
    Then the response should not contain error messages
    And the system logs should record the sync job execution with timestamps
