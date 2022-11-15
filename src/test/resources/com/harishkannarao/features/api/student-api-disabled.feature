@FeatureDisableStudentApi
Feature: student-api-disabled

  Scenario: student-api-get-by-id returns 404 when switched off
    When I perform student-api-get-by-id with a random id
    Then I get a not_found response from student-api-get-by-id
