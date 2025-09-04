# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5262
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:18:03
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail for Approval and Rejection Actions
  As a compliance officer
  I want a full audit trail for all approval and rejection actions
  So that I can ensure compliance and traceability

  Background:
    Given the system has a compliant logging mechanism implemented

  Scenario: Successful logging of approval actions
    Given I am logged into the system as a compliance officer
    When I perform an approval action on a transaction with ID "TXN12345"
    And I access the audit logs
    Then the approval action is logged with the correct user details and timestamp
    And the audit trail displays the approval action "APPROVAL" correctly in the logs
