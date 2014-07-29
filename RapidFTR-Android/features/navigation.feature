Feature: Navigation
	Scenario: Switch Between Tabs
		Given that I am logged in as "field_worker" with password "field_worker"
		And I wait up to 20 seconds for "Basic Identity" to appear
		Then I should see all the Child Registration Tabs
		And I should not see "New Enquiry"
		When I press "Enquiry"
		And I wait up to 20 seconds for "New Enquiry" to appear
		Then I should see "New"
		And I should see "View All"
		And I should not see "Register"
		And I should not see "Search"
		And I should not see "New Registration"