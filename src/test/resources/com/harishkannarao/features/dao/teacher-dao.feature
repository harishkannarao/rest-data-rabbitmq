Feature: teacher-dao

  Scenario: can perform create read update and delete on teacher-dao
    Given a random teacher known as "a_teacher"
    Then I can find by id with "a_teacher" through teacher-dao
    And I can update "a_teacher" with name "some other name" through teacher-dao
    And I can delete "a_teacher" through teacher-dao
