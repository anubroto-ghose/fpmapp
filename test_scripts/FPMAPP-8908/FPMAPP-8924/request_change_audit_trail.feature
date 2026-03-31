# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-8924
# Epic: FPMAPP-8908
# Generated on: 2026-03-31 15:05:35
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail and Versioning of Request Changes
  As a compliance officer
  I want a complete and immutable audit trail of all approval decisions, delegation actions, and request changes
  So that I can ensure compliance and traceability of financial requests

  Background:
    Given a request with ID 12345 has been submitted
    And I am logged in as a user with permission to edit the request

  Scenario: Modify a submitted request and verify audit trail and versioning
    When I navigate to the request details page for request ID 12345
    And I modify the request title to "Updated Request Title - Audit Test"
    And I modify the request description to "Updated description for audit trail verification."
    And I save the changes
    Then the request should be saved successfully
    And the audit log should contain a new entry with the user "compliance_officer" and details of the changes
    And the version history should include a new version reflecting the changes
    And previous versions should remain accessible and immutable
    And the audit log entries should not be editable or deletable

  Scenario: Query audit trail by request and user
    When I query the audit log for request ID 12345
    And I filter the audit log by user "compliance_officer"
    Then the audit log should display entries made by "compliance_officer" for request ID 12345
    And all audit log entries should be immutable
