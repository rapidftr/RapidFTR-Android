Feature: Login feature

  Scenario: No Login Details
    When I press "Log In"
    Then I should see "Username is required"

  Scenario: Incorrect login details
    When I enter the Web Server URL
    And I login with the "invalid" credentials "invalid" and password "invalid"
    Then I should see "Invalid credentials. Please try again!" within "60" seconds
    
  Scenario: Correct Login Details
    Given that I am logged in as "field_worker" with password "field_worker"
    Then I should see "Name"
    And I should see "New Registration"

  Scenario: User able to see last successful login
    When I wait up to 60 seconds for "Basic Identity" to appear
    And I select "Log Out" from the menu
    And I press "Change URL"
    Then I should see the url used for the last successful login

  Scenario: Navigation Back to Login Page is Disabled for Logged-in Users
    Given that I am logged in as "field_worker" with password "field_worker"
    When I go back
    Then I should not see "Log In"
    And I should not see "Sign Up"