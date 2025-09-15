# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-6190
# Epic: FPMAPP-6167
# Generated on: 2025-09-15 06:03:49
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Audit Trail Display for Approval Requests
  As a requester
  I want to see a complete audit trail with timestamped approval actions including user identity and delegation details
  So that I can verify the accuracy and history of my approval requests

  Background:
    Given the requester "requesterUser" is logged into the application
    And the system has approval request with id "123" having an audit trail with actions
      | Timestamp           | ActionType | User          | Remarks                      | DelegationDetails |
      | 2025-09-10T14:30Z   | APPROVAL   | approverUser1 | Approved the request          |                   |
      | 2025-09-11T09:15Z   | DELEGATION | managerUser2  | Delegated to delegateUser3    | delegateUser3     |
      | 2025-09-12T16:45Z   | REJECTION  | delegateUser3 | Rejected due to missing docs  |                   |
      | 2025-09-13T08:00Z   | OVERRIDE   | adminUser4    | Currency override applied     |                   |

  Scenario: Verify audit trail displays all approval actions and delegation info correctly
    When I navigate to the approval requests page
    And I select the approval request with id "123"
    And I open the audit trail panel
    Then I should see audit trail entries
      | ActionType | User          | Timestamp           | DelegationDetails |
      | APPROVAL   | approverUser1 | 2025-09-10T14:30Z   |                   |
      | DELEGATION | managerUser2  | 2025-09-11T09:15Z   | delegateUser3     |
      | REJECTION  | delegateUser3 | 2025-09-12T16:45Z   |                   |
      | OVERRIDE   | adminUser4    | 2025-09-13T08:00Z   |                   |

  # Additional Steps for error handling can be added if API or UI fails, omitted here for brevity
