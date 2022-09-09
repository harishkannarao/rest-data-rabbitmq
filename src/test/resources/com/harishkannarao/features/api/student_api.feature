Feature: student-api

  Scenario: student-api-get-by-id returns student details by ID
    Given a random teacher known as "some_teacher"
    And a random course known as "some_course_1" with teacher "some_teacher"
    And a random course known as "some_course_2" with teacher "some_teacher"
    And a random student known as "some_student_1"
    And a random student known as "some_student_2"
    And student "some_student_1" is registered with course "some_course_1"
    And student "some_student_2" is registered with course "some_course_2"
    When I perform student-api-get-by-id with ID of student "some_student_1"
    Then I get a success response from student-api-get-by-id
    And I see the details of student "some_student_1" from student-api-get-by-id
    And I see the names of courses "some_course_1" from student-api-get-by-id

  Scenario: student-api-get-by-id returns 404 on non-existent id
    When I perform student-api-get-by-id with a random id
    Then I get a not_found response from student-api-get-by-id
