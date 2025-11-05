# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-7216
# Epic: FPMAPP-7183
# Generated on: 2025-11-05 11:11:08
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: FPM Dealsheet Sorting
  As a portfolio analyst
  I want to sort FPM records by project name, customer, and last modified date
  So that I can easily analyze the records

  Background:
    Given I am logged in as a portfolio analyst
    And FPM records are displayed in the record list

  Scenario: Successful sorting by project name
    When I locate the sorting option beside the project name column
    And I click on the sorting option for project name
    Then I observe the order of records after sorting
    And the records should be sorted in ascending order by project name