# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8887
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:33:04
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Audit Log Entry Creation
  As a compliance officer
  I want every approval action to be fully auditable with timestamps and user details
  So that compliance and audit requirements are met

  Background:
    Given the system audit logging feature is enabled
    And I am logged in as a compliance officer

  Scenario: Approval action creates an audit log entry with correct details
    Given I have a pending financial management request
    When I approve the financial management request with comment "Approved after thorough review."
    Then an audit log entry is created for the approval action
    And the audit log entry contains the compliance officer's user ID
    And the audit log entry timestamp is recorded accurately
    And the action type is marked as "approval"
    And the approval comment "Approved after thorough review." is logged
    And the audit log entry is immutable and stored securely
