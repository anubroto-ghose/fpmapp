# Test Case ID: TEST_CASE
# Generated from Jira Ticket: FPMAPP-5259
# Epic: FPMAPP-5250
# Generated on: 2025-09-04 10:19:25
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Approver receives email notification on approval
  As an approver
  I want to receive notifications regarding approvals and rejections
  So that I am informed about actions taken on my pending requests

  Background:
    Given the user "approverUser" is logged in
    And the user has a request "1234" pending approval

  Scenario: Successful email notification on request approval
    When the user approves the pending request "1234"
    Then the user should receive an email notification regarding approval with the request details
