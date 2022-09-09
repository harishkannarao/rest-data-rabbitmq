Feature: student-course-dao

  Scenario: can lookup courses through student and vice versa
    Given a random teacher known as "some_teacher"
    And a random course known as "some_course_1" with teacher "some_teacher"
    And a random course known as "some_course_2" with teacher "some_teacher"
    And a random student known as "some_student_1"
    And a random student known as "some_student_2"
    And student "some_student_1" is registered with course "some_course_1"
    And student "some_student_2" is registered with course "some_course_2"
    Then I can lookup students "some_student_1" by course "some_course_1" through student-course-dao
    Then I can lookup courses "some_course_1" by student "some_student_1" through student-course-dao