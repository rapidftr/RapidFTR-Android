Feature: Sync
  Scenario: Sync All
    Given that I am logged in as "field_worker" with password "field_worker"
    And I have updated form sections
    And I select "Synchronize All" from the menu
    And I wait up to 60 seconds for "Enquiry" to appear
    When I press "Enquiry"
    And I press "Enquiry Criteria Form"
    Then I should see "Updated Child Details"

    When I press "Updated Child Details"
    Then I should see "Updated Name of Child"

    When I press "Child"
    And I press "Basic Identity"
    Then I should see "Updated Family Details"

    When I press "Updated Family Details"
    Then I should see "Updated Father's Name"

  Scenario: Get Child Details
    Given I have a new child record (Name: John Doe, Father: Jonathan Doe) on the server
    And I select "Synchronize All" from the menu
    And I wait up to 60 seconds for "View All" to appear
    And I press "View All"
    Then I should see "John Doe" within "60" seconds

    When I press list item number 1
    And I wait up to 60 seconds for "Basic Identity" to appear
    And I press "Basic Identity"
    And I wait up to 60 seconds for "Updated Family Details" to appear
    And I press "Updated Family Details"
    Then I should see "Jonathan Doe"
