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
    When I enter text "rapidftr" into field with id "username"
    And I enter text "rapidftr" into field with id "password"
    And I enter text "https://test.rapidftr.com" into field with id "url"
    And I press "Log In"
    Then I should see "Login Successful"
    And I should see "Basic Identity"
    And I should see "Name"
    And I should see "Protection Status"
    And I should see "New Registration"

  Scenario: User able to see last successful login
    When I select "Log Out" from the menu
    And I press "Change URL"
    Then I should see "https://test.rapidftr.com"