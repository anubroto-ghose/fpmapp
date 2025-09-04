# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5269
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:15:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Request Notification
  As a financial manager
  I want to receive a notification upon submitting a travel expense approval request
  So that I can confirm that my request has been successfully recorded and processed

  Background:
    Given the user is logged in as "finManager01" with password "SecureP@ssw0rd!"

  Scenario: Submit a travel expense approval request and receive confirmation notification
    When the user navigates to the new approval request form
    And the user fills the travel expense form with:
      | destination   | New York, USA |
      | travelDate    | 2025-10-15    |
      | amount       | 1500          |
      | currency     | USD           |
    And the user submits the approval request
    Then the system should display a notification containing "Request submitted successfully"
    And the notification should include the text "travel expense"
