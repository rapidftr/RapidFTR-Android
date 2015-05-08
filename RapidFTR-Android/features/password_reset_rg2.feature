Feature: Password Reset
  Scenario: Password Reset
    Given that I am logged in as "field_worker" with password "field_worker"
    When I fill the Change Password Form with "field_worker", "rapidftrnew", "rapidftrnew"
    Then I should see "Password Changed Successfully"
    When I select "Log Out" from the menu
    And I login with the "invalid" credentials "field_worker" and password "field_worker"
    Then I should see "Incorrect username or password"
    When I login with the "valid" credentials "field_worker" and password "rapidftrnew"
    And I wait up to 60 seconds for "Basic Identity" to appear
    Then I should see "Name"

  @reinstall
  Scenario: Password Reset Errors (and wrong current password)
    Given that I am logged in as "field_worker" with password "field_worker"
    When I fill the Change Password Form with "", "", ""
    Then I should see "All fields are mandatory"
    When I go back
    And I fill the Change Password Form with "rapidftr1", "rapidftr2", "rapidftr3"
    Then I should see "Password mismatch"
