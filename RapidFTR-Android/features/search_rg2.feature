Feature: Search

  Scenario: Accented characters are properly saved and retrieved in search
    Given that I am logged in as "field_worker" with password "field_worker"
    And I enter "Chïld Nàmê" into the "Name" field of the Child Registration Form
    And I press "Save"
    And I wait up to 60 seconds for "Edit" to appear
    And I press "Search"
    And I wait up to 60 seconds for "Search record" to appear
    When I enter text "Chïld Nàmê" into field with id "search_text"
    And I press "Go"
    Then I should see "Name: Chïld Nàmê" within "60" seconds

  @ignore
  Scenario: Highlighted fields are used to show summary of child records in search
    Given I have a new child record (Name: John Doe, Father: Jonathan Doe) on the server
    And I have form sections with the highlighted field "Father"
    And I select "Synchronize All" from the menu
    And I wait up to 60 seconds for "Child Records successfully synchronized" to appear
    And I wait up to 60 seconds for "Search" to appear
    And I press "Search"
    When I enter text "John Doe" into field with id "search_text"
    And I press "Go"
    Then I should see "Name: John Doe" within "60" seconds
    And I should see "Father's Name: Jonathan Doe"

    When I press list item number 1
    And I wait up to 60 seconds for "Basic Identity" to appear
    And I press "Basic Identity"
    And I wait up to 60 seconds for "Family Details" to appear
    And I press "Family Details"
    Then I should see "Jonathan Doe"
