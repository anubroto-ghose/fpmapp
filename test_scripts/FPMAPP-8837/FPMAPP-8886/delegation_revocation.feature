# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8886
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:33:46
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Revocation of delegation by original approver
  
  As an original approver
  I want to revoke my delegation
  So that the delegate user no longer has approval rights and the action is logged and notified

  Background:
    Given a delegation exists with a valid delegate user and active time period
    And the original approver is logged into the system

  Scenario: Successfully revoke an active delegation
    When the original approver navigates to the delegation management interface
    And locates the active delegation
    And revokes the delegation
    Then the delegation is successfully revoked
    And the delegate user no longer has approval rights
    And the revocation action is logged with user details and timestamps
    And the delegate user receives a notification about the revocation
