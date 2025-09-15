# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6237
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:28:22
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: INR to JPY Currency Conversion History Display
  
  As a user with role-based approval permissions
  I want to see accurate and formatted INR to JPY conversion entries in the transaction history
  So that I can verify data integrity and audit trail references post FPMAPP-6167 changes

  Background:
    Given the system contains at least one INR to JPY transaction with audit trail data
    And the user is logged in with role-based approval permissions

  Scenario: Verify correct data is displayed for INR to JPY conversion entry
    When the user navigates to the transaction history page
    Then the transaction history should display at least one INR to JPY conversion entry
    And the date format on the entry should be "DD-MM-YYYY"
    And the amount shown in INR should be correctly formatted with the Indian Rupee currency symbol "₹"
    And the amount shown in JPY should be correctly formatted with the Yen currency symbol "¥" and calculated with up-to-date exchange rate
    And the status of the conversion should be displayed correctly
    And an audit trail reference should be visible and match system records

  Scenario Outline: Validate multiple INR-JPY conversion entries
    Given the following INR to JPY conversion entries exist:
      | transactionDate | amountFrom | amountTo | status    | auditReference |
      | <date>          | <amtFrom>  | <amtTo>  | <status>  | <auditRef>    |
    When the user navigates to the transaction history page
    Then the transaction history entry for date "<date>" should exist
    And the INR amount should be "₹<formattedFrom>"
    And the JPY amount should be "¥<formattedTo>"
    And the status should be "<status>"
    And the audit trail reference should be "<auditRef>"

    Examples:
      | date       | amtFrom | amtTo    | status   | auditRef      | formattedFrom | formattedTo  |
      | 01-09-2025 | 10000.5 | 1650000.75 | Approved | AuditID-123456 | 10,000.50    | 1,650,000.75 |
      | 10-09-2025 | 5000.00 | 825000.00 | Pending  | AuditID-654321 | 5,000.00     | 825,000.00   |
