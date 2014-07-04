Feature: Register Child
  
  Scenario: Default Form Sections are Displayed
    Given that I am logged in as "field_worker" with password "field_worker"
    And I press "Basic Identity"
    Then I should see all the default form sections
  
  Scenario: Enter, Save and Edit Child Details
    When I wait up to 20 seconds for "Basic Identity" to appear
    And I enter "Child Name" into the "Name" field of the Child Registration Form
  	And I press "Save"
    And I wait up to 20 seconds for "Saved record successfully" to appear
  	Then I should see "Edit"
  	And I should see "Child Name"
  	When I press "Edit"
    And I wait up to 20 seconds for "Save" to appear
  	Then I should not see "Edit"
  	When I enter "Updated Child Name" into the "Name" field of the Child Registration Form
  	And I press "Save"
  	And I wait up to 20 seconds for "Saved record successfully" to appear
  	Then I should see "Edit"
  	And I should see "Updated Child Name"

  Scenario: Prompt to Save on Navigating Away
    When I enter "Child Name" into the "Name" field of the Child Registration Form
    And I select "Log Out" from the menu
    And I wait up to 20 seconds for "Choose an action" to appear
    Then I should see "Save"
    Then I should see "Discard"
    Then I should see "Cancel"