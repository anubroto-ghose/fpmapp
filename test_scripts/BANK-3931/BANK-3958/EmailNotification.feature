# Test Case ID: TEST_CASE
# Generated from Jira Ticket: BANK-3958
# Epic: BANK-3931
# Generated on: 2025-07-18 13:44:15
#
# This is an auto-generated Cucumber feature file.
# Modify with caution as changes may be overwritten.

Feature: Email notification on submission of financial entries

  Scenario: Submit financial entry
    Given the user is logged in as a system user
    When the user submits a financial entry
    Then an email notification is sent upon submission of the financial entry

  @Given("the user is logged in as a system user")
  public void userIsLoggedInAsSystemUser() {
      // Code to log in the system user
  }

  @When("the user submits a financial entry")
  public void userSubmitsFinancialEntry() {
      // Code to submit a financial entry
  }

  @Then("an email notification is sent upon submission of the financial entry")
  public void emailNotificationIsSent() {
      // Code to check email notification
  }
