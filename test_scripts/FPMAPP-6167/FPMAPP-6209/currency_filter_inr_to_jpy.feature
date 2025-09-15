# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6209
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:48:56
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Currency Conversion Filter for INR to JPY Transactions with Role-Based Approvals and Audit Trail
  
  As a user with approval role
  I want to filter transaction history for INR to JPY conversions
  So that I see only relevant conversion records and audit trails respecting my role

  Background:
    Given currency conversion history exists with multiple currency pairs including INR to USD and INR to JPY
    And role-based approvals and audit trails are enabled
    And I am logged in as a user with "ROLE_APPROVER" and "ROLE_VIEW_INR_TO_JPY"

  @Regression
  Scenario: Filter transaction history for INR to JPY currency conversions
    When I navigate to the transaction history page
    And I apply the currency filter selecting "INR to JPY"
    Then I should see only transactions where fromCurrency is "INR" and toCurrency is "JPY"
    And I should not see transactions for other currency pairs
    And I should be able to access audit trails for each displayed transaction
    And I should only see audit records permitted by my approval role
    And the displayed data should reflect real-time currency integration
    And role-based access control is enforced on transaction visibility
    And the filter functionality does not disrupt other system features

  
  # Step Definitions for this feature should mock:
  # - CurrencyConvertionController responses
  # - FpmCommonController approval and audit methods
  # - User profile role checks
  
