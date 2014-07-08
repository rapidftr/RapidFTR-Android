Feature: Login feature

  Scenario: No Login Details
    When I press "Log In"
    Then I should see "Username is required"

  Scenario: Web Server Not Specified
    When I enter the username "invalid" and password "invalid"
    And I press "Log In"
    Then I should see "Online login failed, will try to login offline"

  Scenario: Incorrect login details
    When I enter the Web Server URL
    And I login with the "invalid" credentials "invalid" and password "invalid"
    Then I should see "Invalid credentials. Please try again!"
    
  Scenario: Correct Login Details
    Given that I am logged in as "field_worker" with password "field_worker"
    Then I should see "Name"
    And I should see "New Registration"

  Scenario: User able to see last successful login
    When I wait up to 20 seconds for "Basic Identity" to appear
    And I select "Log Out" from the menu
    And I press "Change URL"
    Then I should see the url used for the last successful login

  Scenario: Navigation Back to Login Page is Disabled for Logged-in Users
    Given that I am logged in as "field_worker" with password "field_worker"
    When I go back
    Then I should not see "Log In"
    And I should not see "Sign Up"

  @reinstall
  Scenario: Password Reset
    Given that I am logged in as "field_worker" with password "field_worker"
    When I fill the Change Password Form with "field_worker", "rapidftrnew", "rapidftrnew"
    Then I should see "Password Changed Successfully"
    When I select "Log Out" from the menu
    And I login with the "invalid" credentials "field_worker" and password "field_worker"
    Then I should see "Incorrect username or password"
    When I login with the "valid" credentials "field_worker" and password "rapidftrnew"
    And I wait up to 20 seconds for "Basic Identity" to appear
    Then I should see "Name"

  @reinstall
  Scenario: Password Reset Errors (and wrong current password)
    Given that I am logged in as "field_worker" with password "field_worker"
    When I fill the Change Password Form with "", "", ""
    Then I should see "All fields are mandatory"
    When I go back
    And I fill the Change Password Form with "rapidftr1", "rapidftr2", "rapidftr3"
    Then I should see "Password mismatch"
