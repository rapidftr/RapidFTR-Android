Feature: Unverified User
  As an unregistered user
  I should be able to create an unverified user account
  And use it to log in and register children

  Scenario: Create unverified user account with missing user details
    When I press "Sign Up!"
	Then I should see the following fields
	  | fieldname         |
	  | Username          |
	  | Password          |
	  | Re-enter Password |
	  | Full name         |
	  | Organisation      |
	When I press "Sign Up!"
	Then I should see "Username is required"
	When I press enter
	Then I should see "Password is required"
	When I press enter
	Then I should see "Re-type password"
	When I press enter
	Then I should see "Full name required"
	When I press enter
	Then I should see "Organisation is required"

  Scenario: Create unverified user account with valid user details
    When I press "Sign Up!"
    And I enter the following form details
	  | fieldname         | details         |
	  | username          | unverified_user |
	  | password          | password        |
	  | confirm_password  | password        |
	  | full_name         | Unverified User |
	  | organisation      | Unicef          |
    And I press "Sign Up!"
    Then I should see "You are registered. Login with the username unverified_user"
    And I should see "Log In"
    And I should see "Sign Up!"

  Scenario: Sync with unverified user account
    When I enter the username "unverified_user" and password "password"
    And I press "Log In"
    Then I should see "Offline login successful"
    When I wait up to 60 seconds for "Basic Identity" to appear
    And I press the menu button
    Then I should not see "Change Password"
    When I select "Synchronize All" from the menu
    Then I should see "Enter sync location"
    