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