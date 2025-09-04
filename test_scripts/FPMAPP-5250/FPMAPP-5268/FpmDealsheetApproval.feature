# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5268
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:14:33
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Role-Based Approval Workflow for Deal Sheets
  As a financial manager with approval rights
  I want to approve deal sheet requests
  So that deal sheets are processed correctly and requesters notified

  Background:
    Given the user "fin_manager_user" is logged in with role "Financial Manager"
    And there is a pending deal sheet request with ID 1001 and requester email "requester@example.com"

  @SmokeTest @HighPriority
  Scenario: Approve a deal sheet request successfully
    When the user navigates to the approvals section
    And selects the deal sheet request with ID 1001
    And clicks the "Approve" button
    Then the deal sheet request with ID 1001 should be approved
    And the requester with email "requester@example.com" should receive an approval notification
