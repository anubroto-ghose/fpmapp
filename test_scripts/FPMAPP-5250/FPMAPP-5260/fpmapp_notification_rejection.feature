# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5260
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 13:52:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: In-App Notification on Approval Request Rejection
  As an approver,
  I want to receive immediate in-app notifications when I reject an approval request,
  So that I am informed about the action and its status.

  Background:
    Given the user "approverUser" is logged into the FPM application
    And the user has a pending approval request with ID "5001"

  Scenario: Approver rejects a pending approval request and receives notification
    When the approver navigates to the pending approvals page
    And the approver rejects the approval request with ID "5001"
    Then the approval request with ID "5001" should have status "REJECTED"
    And the approver should receive an in-app notification regarding the rejection
    And the notification message should contain the request ID "5001" and the word "rejected"

  @negative
  Scenario: Rejecting a non-existing approval request
    When the approver attempts to reject the approval request with ID "9999"
    Then an error message "Approval request not found" should be displayed
    And no new in-app notification should be generated

  @edge
  Scenario: Rejection notification appears in notification list filtered by rejection type
    When the approver views the in-app notifications filtered by type "REJECTION"
    Then any notification listed should contain the word "rejected"


# Step Definitions (Java) to be implemented separately to wire automation with this feature file.