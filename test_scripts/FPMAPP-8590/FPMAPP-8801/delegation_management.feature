# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8801
# Epic: FPMAPP-8590
# Generated on: 2026-03-26 15:37:26
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Management - Enforce Scope and Duration Limits
  As a manager authorized to delegate approval rights
  I want the system to enforce limits on delegation scope and duration
  So that delegations comply with organizational policies

  Background:
    Given the user is logged in as an authorized delegator
    And the Delegation Management form is open

  @scope-limit
  Scenario: Attempt to create a delegation with scope exceeding allowed limits
    When the user enters a delegation scope of 10 projects
    And the user enters a delegation duration of 10 days
    And the user submits the delegation form
    Then the system rejects the delegation creation
    And the system displays a validation error "Scope exceeds allowed limit"
    And the delegation record is not saved
    And an audit log is created capturing the failed delegation attempt with scope details

  @duration-limit
  Scenario: Attempt to create a delegation with duration exceeding allowed limits
    When the user enters a delegation scope of 3 projects
    And the user enters a delegation duration of 60 days
    And the user submits the delegation form
    Then the system rejects the delegation creation
    And the system displays a validation error "Duration exceeds allowed limit"
    And the delegation record is not saved
    And an audit log is created capturing the failed delegation attempt with duration details

  @valid-delegation
  Scenario: Create a delegation within allowed scope and duration
    When the user enters a delegation scope of 3 projects
    And the user enters a delegation duration of 15 days
    And the user submits the delegation form
    Then the system accepts the delegation creation
    And no validation errors are displayed
    And the delegation record is saved with scope 3 and duration 15
    And an audit log is created capturing the successful delegation creation

  @ui-validation
  Scenario: UI provides real-time validation feedback on scope and duration fields
    When the user enters a delegation scope of 10 projects
    Then the system displays a real-time validation error "Scope exceeds allowed limit"
    When the user enters a delegation duration of 60 days
    Then the system displays a real-time validation error "Duration exceeds allowed limit"
    When the user corrects the delegation scope to 3 projects
    And the user corrects the delegation duration to 15 days
    Then the system removes the validation errors
