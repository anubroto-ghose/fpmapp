# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7218
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:10:19
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Sorting FPM records
  As a portfolio analyst 
  I want to sort FPM records by project name, customer, and last modified date
  So that I can view the records in a desired order

  Scenario: Error handling when sorting fails
    Given User is logged in as a portfolio analyst
    And FPM records are displayed in the record list
    When I attempt to sort records by project name
    Then an error message should be displayed indicating that sorting has failed