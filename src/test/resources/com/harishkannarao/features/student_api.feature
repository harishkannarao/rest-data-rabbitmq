Feature: student-api

  Scenario: student-api-get-by-id returns student details by ID
    Given a random teacher known as "some_teacher"
    And a random course known as "some_course_1" with teacher "some_teacher"
    And a random course known as "some_course_2" with teacher "some_teacher"
    And a random student known as "some_student_1"
    And a random student known as "some_student_2"
    And student "some_student_1" is registered with course "some_course_1"
    And student "some_student_2" is registered with course "some_course_2"
    When student-api-get-by-id is performed with student "some_student_1"
    Then student-api-get-by-id returns a success response
    And student-api-get-by-id has details of student "some_student_1"
    And student-api-get-by-id has names of courses "some_course_1"
