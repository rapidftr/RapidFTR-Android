Feature: Register Child
  Scenario: Default Form Sections are Displayed
    Given that I am logged in as "field_worker" with password "field_worker"
    And I press "Basic Identity"
    Then I should see "Family details"
    And I should see "Care Arrangements"
    And I should see "Separation History"
    And I should see "Protection Concerns"
    And I should see "Other Interviews"
    And I should see "Other Tracing Info"
    And I should see "Interview Details"
    And I should see "Photos and Audio"
  
  Scenario: Enter, Save and Edit Child Details
    When I enter "Child Name" into the "Name" field of the Child Registration Form
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