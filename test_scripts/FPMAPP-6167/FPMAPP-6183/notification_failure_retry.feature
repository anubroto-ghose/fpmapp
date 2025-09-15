# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6183
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:08:55
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Notification failure and retry handling with error logging
  As a user involved in approvals
  I want the system to automatically retry failed notifications and log all errors
  So that notifications are eventually delivered and failures are traceable

  Background:
    Given the SMTP service is temporarily disabled or misconfigured
    And the notification retry and error logging mechanisms are enabled

  Scenario: User changes an approval status triggering notification with SMTP failure and recovery
    When the user changes the approval status on approval id "5001" to "APPROVED"
    Then the system detects failed email delivery due to SMTP failure
    And the system retries to send the notification email according to the configured retry policy
    And an error log entry for the notification failure is created with the approval id "5001"
    When the SMTP service is restored
    Then the notification email is eventually sent successfully for approval id "5001"
    And the system records the successful notification delivery
