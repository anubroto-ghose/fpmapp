# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6206
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:51:07
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Conversion History Display
  As a user of the FPM application
  I want to view accurate INR to JPY currency conversion history entries
  So that I can verify conversion amounts, statuses, and related audit trail data

  Background:
    Given the system has at least one completed INR to JPY conversion transaction
      | transactionId | fromCurrency | toCurrency | amountFrom | amountTo | conversionDate | status    |
      | TXN1001       | INR          | JPY        | 5000       | 7500     | 15-09-2025     | Completed |
    And the audit trail for transaction TXN1001 includes:
      | action   | user           | timestamp           |
      | Submitted| requester.user | 14-09-2025 08:00:00 |
      | Approved | approver.user  | 15-09-2025 06:45:00 |

  Scenario: Verify correct display of INR to JPY conversion entry with audit trail for authorized user
    Given I am logged in as a user with "ROLE_COMPLIANCE_OFFICER"
    When I navigate to the transaction history page
    Then I should see a conversion entry with transaction ID "TXN1001"
    And the conversion date should be formatted as "DD-MM-YYYY"
    And the amount in INR should be displayed correctly with currency symbol
    And the amount in JPY should be calculated correctly based on real-time currency integration
    And the status should be displayed as "Completed"
    And I can view the complete audit trail for transaction "TXN1001"

  Scenario: Verify audit trail action entries are accurate and timestamp format is consistent
    Given I am logged in as a user with "ROLE_COMPLIANCE_OFFICER"
    When I view the audit trail for transaction "TXN1001"
    Then the audit trail should include an action "Submitted" by "requester.user" with timestamp format "DD-MM-YYYY HH:mm:ss"
    And the audit trail should include an action "Approved" by "approver.user" with timestamp format "DD-MM-YYYY HH:mm:ss"

  Scenario: Verify unauthorized user cannot view audit trail or approval details
    Given I am logged in as a user with "ROLE_BASIC_USER"
    When I navigate to the transaction history page
    Then I should see the conversion entry with transaction ID "TXN1001"
    But I should NOT see the option to view the audit trail for transaction "TXN1001"
