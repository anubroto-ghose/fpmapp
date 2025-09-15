# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6238
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:27:49
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: INR to JPY Currency Conversion History Empty State
  As a logged-in user with role-based approval restrictions
  I want to see a clear message when there are no transactions for INR to JPY conversions
  So that I am correctly informed and no erroneous or blocking UI appears

  Background:
    Given the user is logged in with role "ROLE_APPROVER"

  Scenario: Display empty state message when no INR to JPY currency conversion transactions exist
    When the user navigates to the transaction history page
    And the user applies filter from currency "INR" to currency "JPY"
    Then the page displays a message "No INR to JPY transactions found"
    And no transactions are displayed
    And no approval blocking overlays are present
    And an audit trail event for empty state display is recorded
