# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8801
# Epic: FPMAPP-8590
# Generated on: 2026-03-27 08:01:51
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Delegation Rules Enforcement
  As a manager delegating approval rights
  I want the system to enforce limits on delegation scope and duration
  So that delegation rules are respected and audit logs are maintained

  Background:
    Given I am logged in as an authorized delegator
    And I have opened the Delegation Management form

  @scope_limit
  Scenario: Attempt to create a delegation with scope exceeding allowed limits
    When I enter a delegation scope of 7 projects
    And I enter a delegation duration of 15 days
    And I submit the delegation form
    Then I should see a validation error "Scope exceeds allowed limit"
    And the delegation should not be created

  @duration_limit
  Scenario: Attempt to create a delegation with duration exceeding allowed limits
    When I enter a delegation scope of 3 projects
    And I enter a delegation duration of 45 days
    And I submit the delegation form
    Then I should see a validation error "Duration exceeds allowed limit"
    And the delegation should not be created

  @successful_creation
  Scenario: Create a delegation within allowed scope and duration
    When I enter a delegation scope of 4 projects
    And I enter a delegation duration of 20 days
    And I submit the delegation form
    Then I should see a success message "Delegation created successfully"
    And the delegation should be saved with scope 4 and duration 20
    And an audit log should be created for the delegation

  @real_time_validation
  Scenario: UI provides real-time validation feedback on scope and duration fields
    When I enter a delegation scope of 10 projects
    Then I should see a validation error "Scope exceeds allowed limit"
    When I enter a delegation duration of 60 days
    Then I should see a validation error "Duration exceeds allowed limit"
    When I correct the delegation scope to 3 projects
    And I correct the delegation duration to 15 days
    Then I should see no validation errors for scope and duration
