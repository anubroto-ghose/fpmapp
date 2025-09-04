# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5269
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:14:02
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approval Request Submission Notification
  As a financial manager
  I want to receive a confirmation notification upon submission of a travel expense request
  So that I am assured my request has been successfully submitted

  Background:
    Given a user with role "ROLE_FINANCIAL_MANAGER" is logged in

  Scenario: Validate notification upon travel expense request submission
    When the user navigates to the travel expense request submission page
    And submits a new travel expense request with destination "New York, NY", start date "2024-07-01", end date "2024-07-05", amount "1500", and currency "USD"
    Then the user should receive a notification confirming the request submission
