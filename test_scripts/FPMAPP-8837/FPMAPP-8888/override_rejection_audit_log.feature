# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8888
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:32:21
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Override Rejection Audit Logging and Alert Flagging
  As a compliance officer
  I want every override rejection action to be fully auditable with timestamps and user details
  So that compliance and audit requirements are met

  Background:
    Given a user "overrideUser" with override permissions is logged in
    And override audit logging is enabled
    And alert flagging for override actions is enabled

  Scenario: Perform override rejection and verify audit log entry and alert flag
    When the user performs an override rejection on financial request "REQ-1001"
    Then an audit log entry is created with:
      | userId       | overrideUser       |
      | actionType   | override rejection |
    And the audit log entry has a timestamp within the last 5 seconds
    And the audit log entry has an alert flag set
    And the audit log entry is immutable and stored securely
    And the alert flag triggers configured notifications or compliance alerts
