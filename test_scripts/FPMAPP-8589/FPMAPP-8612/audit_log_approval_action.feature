# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8612
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 08:05:54
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Log Creation for Approval Actions
  As an auditor
  I want comprehensive audit trails for all approval-related actions
  So that I can verify the integrity and completeness of approval logs

  Background:
    Given a user with auditor role is logged in
    And an approval action is available on a deal sheet with ID "deal-456"
    And the audit logging framework is integrated and the audit_logs table is ready

  Scenario: Approval action triggers audit log creation with complete metadata
    When the auditor performs an approval action on the deal sheet "deal-456"
    Then an audit log entry is created with action_type "approval"
    And the audit log entry contains the correct user_id
    And the audit log entry contains a valid timestamp
    And the audit log entry contains delegation_info if any
    And the audit log entry contains the related request identifier "deal-456"
    And the audit log entry is stored persistently and can be queried
    And the audit log entry data integrity is maintained with no missing or corrupted fields
