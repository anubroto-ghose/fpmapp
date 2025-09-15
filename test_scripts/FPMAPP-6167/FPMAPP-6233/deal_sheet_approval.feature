# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6233
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 05:30:53
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Hierarchical Role-Based Deal Sheet Approval with Audit Trail
  
  Background:
    Given a deal sheet exists with ID 12345 and status "Pending Approval"
    And a logged in user "managerUser" has role "Manager" with permission to approve
    And the approval workflow hierarchy is configured: Manager -> Director

  Scenario: Manager approves a deal sheet successfully
    When the user sends a POST request to "/deal-sheet/approve" with:
      | action       | approve   |
      | dealSheetId  | 12345     |
      | roleId       | MANAGER   |
    Then the response status should be 200
    And the response JSON should contain:
      | approvalStatus      | Approved by Manager |
      | currentApproverRole | DIRECTOR            |
      | auditLogId          | <any-positive-integer> |
    And an audit log entry should be created with:
      | approvalId         | 12345     |
      | userId             | any       |
      | action             | approve   |
      | previousStatus     | Pending Approval |
      | newStatus          | Approved by Manager |
    And the deal sheet status updates to "Approved by Manager"
    And the current approver role updates to "DIRECTOR"

  Scenario Outline: Unauthorized role attempts to approve and is rejected
    Given a logged in user "<username>" has role "<role>"
    When the user sends a POST request to "/deal-sheet/approve" with:
      | action       | approve   |
      | dealSheetId  | 12345     |
      | roleId       | <role>    |
    Then the response status should be 403
    And the response JSON should contain error message "User role not authorized to approve this deal sheet"

    Examples:
      | username    | role     |
      | juniorUser  | JUNIOR   |
      | internUser  | INTERN   |
