Feature: Login feature
  Scenario: No Login Details
    And I press "Log In"
    Then I see "Username is required"