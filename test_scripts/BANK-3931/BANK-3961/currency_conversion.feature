# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-3961
# Epic: BANK-3931
# Generated on: 2025-07-18 13:40:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-Time Currency Conversion

  Scenario: Currency conversion reflects live exchange rates
    Given the currency rate synchronization job has been executed successfully
    And the base currency is "USD"
    And the target currency is "INR"
    When I perform a currency conversion of "100 USD"
    Then the conversion should reflect the latest live exchange rate
    And the result should be "Converted Amount: 8265.00 INR (Rate Used: 1 USD = 82.65 INR as of 2025-07-18 08:00 IST)"
