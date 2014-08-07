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
