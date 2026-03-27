# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8879
# Epic: FPMAPP-8837
# Generated on: 2026-03-27 14:38:06
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time approval status and delegation notes update
  As a requester
  I want to see real-time status updates of my approval requests
  So that I can track the current approval stage and delegation notes without refreshing the page

  Background:
    Given the user is logged in as a requester
    And the approval request has delegation or override notes associated
    And real-time updates are enabled and connected

  Scenario: UI updates to show current approval stage and delegation notes in real-time
    When a backend event updates the approval stage to "Approved by Manager"
    And adds delegation notes "Delegated to Senior Analyst due to workload."
    Then the UI should update to show the approval stage as "Approved by Manager"
    And the UI should display delegation notes "Delegated to Senior Analyst due to workload."
    And the UI should not show any stale or inconsistent data
    And the UI updates should occur in real-time without user intervention
