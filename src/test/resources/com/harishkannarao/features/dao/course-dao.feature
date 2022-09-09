Feature: course-dao

  Scenario: can get courses using teacher id
    Given a random teacher known as "teacher_1"
    And a random teacher known as "teacher_2"
    And a random course known as "some_course_1" with teacher "teacher_1"
    And a random course known as "some_course_2" with teacher "teacher_2"
    And a random course known as "some_course_3" with teacher "teacher_1"
    Then I can get courses "some_course_1,some_course_3" by teacher "teacher_1" through course-dao
