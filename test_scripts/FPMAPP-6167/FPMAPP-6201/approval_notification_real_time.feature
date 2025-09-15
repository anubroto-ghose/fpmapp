# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6201
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:55:27
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time In-App Notifications for Approval Events
  
  As a user of the FPM application
  I want to receive real-time in-app notifications about approvals, rejections, delegation, or overrides
  So that I do not need to refresh the page to see current updates

  Background:
    Given the user "approverUser" is logged into the FPM_UI with WebSocket enabled
    And the user navigates to the Approvals page
    And the notification area is visible on the page

  Scenario: Display approval granted notification in real-time without page refresh
    When an approval event "APPROVAL_GRANTED" for deal "12345" occurs
    Then the in-app notification area displays the message "Your approval for deal #12345 has been granted by user approverUser."
    And a POST request is made to "/notifications/in-app" API endpoint
    And the API response indicates success

  Scenario: Display delegation notification in real-time without page refresh
    When a delegation event "DELEGATION_ASSIGNED" for approval "54321" occurs
    Then the in-app notification area displays the message "Approval #54321 has been delegated to you by user approverUser."
    And a POST request is made to "/notifications/in-app" API endpoint
    And the API response indicates success

  Scenario: Display approval override notification in real-time without page refresh
    When an override event "CURRENCY_OVERRIDE" for currency "USD" occurs
    Then the in-app notification area displays the message "Currency rate for USD has been overridden by adminUser."
    And a POST request is made to "/notifications/in-app" API endpoint
    And the API response indicates success
