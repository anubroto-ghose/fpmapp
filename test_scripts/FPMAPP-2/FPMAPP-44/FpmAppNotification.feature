# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-44
# Epic: FPMAPP-2
# Generated on: 2026-03-06 12:30:33
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Notification for Status Changes

  Scenario: User receives notification for status change
    Given the requester has notifications enabled in their account settings
    When the requester submits a request
    And the request status changes
    Then the requester should receive a notification for the status change
