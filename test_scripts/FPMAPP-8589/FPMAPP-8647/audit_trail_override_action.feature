# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8647
# Epic: FPMAPP-8589
# Generated on: 2026-03-27 07:41:59
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Immutability and Access Control on Override Actions
  As a compliance officer
  I want comprehensive audit trails for all approval-related override actions
  So that audit logs remain immutable, secure, and accessible only to authorized users

  Background:
    Given the user is authenticated as a compliance officer
    And the system has the modified POST /fpm/travel/approval API deployed
    And the Audit_Logs table schema is extended with required columns and indexes
    And audit logs are secured with role-based access controls

  Scenario: Perform override action and verify audit log creation
    When the compliance officer performs an override action on a travel request with id "TRAVEL-12345" and reason "Urgent business need"
    Then an audit log entry is created with action_type "override" and all required metadata

  Scenario: Prevent direct modification of audit log entries
    Given an audit log entry exists for travel request id "TRAVEL-12345" with action_type "override"
    When an attempt is made to modify the audit log entry directly in the database
    Then the modification is prevented by database constraints or application logic

  Scenario: Deny unauthorized access to audit logs
    Given a user without proper permissions
    When the user attempts to access audit logs
    Then access is denied

  Scenario: Allow authorized users to query audit logs
    Given an authorized compliance officer
    When the user queries audit logs by entity id "TRAVEL-12345" and action type "override"
    Then the audit logs are returned efficiently
    And audit logs remain immutable and secure
