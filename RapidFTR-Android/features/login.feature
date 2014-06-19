Feature: Login feature

  Scenario: No Login Details
    When I press "Log In"
    Then I should see "Username is required"

  Scenario: Incorrect Login Details
    When I enter text "invalid" into field with id "username"
    And I enter text "invalid" into field with id "password"
    And I press "Log In"
    Then I should see "Incorrect username or password"

  Scenario: Correct Login Details
    Given that I am logged in as "field_worker" with password "field_worker"
    Then I should see "Name"
    And I should see "Protection Status"
    And I should see "New Registration"

  Scenario: User able to see last successful login
    When I select "Log Out" from the menu
    And I press "Change URL"
    Then I should see "https://test.rapidftr.com"

  Scenario: Navigation Back to Login Page is Disabled for Logged-in Users
    Given that I am logged in as "field_worker" with password "field_worker"
    When I go back
    Then I should not see "Log In"
    And I should not see "Sign Up"

  @reinstall
  Scenario: Password Reset
    Given that I am logged in as "field_worker" with password "field_worker"
    When I select "Change Password" from the menu
    And I enter text "field_worker" into field with id "current_password"
    And I enter text "rapidftrnew" into field with id "new_password"
    And I enter text "rapidftrnew" into field with id "new_password_confirm"
    And I press "Change Password"
    Then I should see "Password Changed Successfully"
    When I select "Log Out" from the menu
    And I enter text "field_worker" into field with id "username"
    And I enter text "field_worker" into field with id "password"
    And I press "Log In"
    Then I should see "Incorrect username or password"
    When I enter text "" into field with id "username"
    And I enter text "field_worker" into field with id "username"
    And I enter text "" into field with id "password"
    And I enter text "rapidftrnew" into field with id "password"
    And I press "Log In"
    And I wait up to 20 seconds for "Basic Identity" to appear
    And I select "Change Password" from the menu
    And I enter text "rapidftrnew" into field with id "current_password"
    And I enter text "field_worker" into field with id "new_password"
    And I enter text "field_worker" into field with id "new_password_confirm"
    And I press "Change Password"
    Then I should see "Password Changed Successfully"

  @reinstall
  Scenario: Password Reset Errors (and wrong current password)
    Given that I am logged in as "field_worker" with password "field_worker"
    When I select "Change Password" from the menu
    And I enter text "" into field with id "current_password"
    And I enter text "" into field with id "new_password"
    And I enter text "" into field with id "new_password_confirm"
    And I press "Change Password"
    Then I should see "All fields are mandatory"
    And I enter text "rapidftr1" into field with id "current_password"
    And I enter text "rapidftr2" into field with id "new_password"
    And I enter text "rapidftr3" into field with id "new_password_confirm"
    And I press "Change Password"
    Then I should see "Password mismatch"
