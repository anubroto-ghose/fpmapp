# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8793
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:31:38
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Real-time UI feedback on sync failure
  As a user of the FPM Application
  I want the UI to gracefully handle real-time sync failures
  So that I receive clear notifications without page reloads and can retry actions

  Background:
    Given the user "testuser" is logged into the FPM Application
    And the WebSocket connection is disrupted

  @approval
  Scenario: Approval action with real-time sync failure
    When the user performs an approval action on a pending deal
    Then the UI should display a clear notification about the sync failure
    And the page should not reload
    And the user should be offered options to retry or manually refresh
    And the audit trail and delegation components remain stable

  @currencyOverride
  Scenario: Currency override action with real-time sync failure
    When the user performs a currency override action with valid data
    Then the UI should display a clear notification about the sync failure
    And the page should not reload
    And the user should be offered options to retry or manually refresh
    And the audit trail and delegation components remain stable
