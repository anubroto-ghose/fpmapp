# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7214
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:11:49
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: FPM Record Detail View Retrieval

  Scenario: User retrieves FPM record details
    Given the user is on the detailed view page of FPM record
    When the user observes the data displayed in the detailed view
    Then the data displayed matches the expected project data format
