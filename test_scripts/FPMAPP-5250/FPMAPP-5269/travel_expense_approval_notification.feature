# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5269
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:39:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Travel Expense Approval Request Submission Notification
  
  As a financial manager
  I want to receive a notification when I submit a new travel expense approval request
  So that I have confirmation that my request was submitted successfully

  Background:
    Given a financial manager user is logged in

  Scenario: Submit a new travel expense approval request and receive confirmation notification
    When the user submits a travel expense approval request with the following details:
      | destination | New York       |
      | purpose     | Client Meeting |
      | estimatedCost | 1250.75      |
    Then the system processes the request successfully
    And the user receives a notification confirming the request submission

  Scenario Outline: Submit travel expense with various cost values
    When the user submits a travel expense approval request with the following details:
      | destination   | <destination>   |
      | purpose       | <purpose>       |
      | estimatedCost | <estimatedCost> |
    Then the system processes the request successfully
    And the user receives a notification confirming the request submission

    Examples:
      | destination   | purpose           | estimatedCost |
      | London        | Business Trip     | 1000.00      |
      | San Francisco | Conference        | 2300.50      |
      | Tokyo         | Training Session  | 1450.25      |
