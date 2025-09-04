# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5263
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:17:37
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail for Rejection Actions
  
  As a compliance officer
  I want a full audit trail for all approval and rejection actions
  So that I can ensure regulatory compliance and traceability

  Background: 
    Given the system has a compliant logging mechanism implemented

  @high
  Scenario: Successful logging of rejection action
    Given I am logged into the system as a compliance officer
    When I perform a rejection action on a transaction with id "txn12345" and reason "Compliance check failed due to policy violation"
    Then the rejection action should be logged with correct user details and timestamp
    And the audit trail should display the rejection action correctly in the logs

  
  
  # Step Definitions should handle these Gherkin steps and assert accordingly
