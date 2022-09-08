package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.dto.StudentWithCourseResponseDto;
import com.harishkannarao.restdatarabbitmq.entity.Student;
import com.harishkannarao.restdatarabbitmq.steps.holder.DataFixturesHolder;
import com.harishkannarao.restdatarabbitmq.steps.holder.StudentApiHolder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentApiSteps extends AbstractBaseSteps {
    private final DataFixturesHolder dataFixturesHolder;
    private final StudentApiHolder studentApiHolder;

    public StudentApiSteps(DataFixturesHolder dataFixturesHolder, StudentApiHolder studentApiHolder) {
        this.dataFixturesHolder = dataFixturesHolder;
        this.studentApiHolder = studentApiHolder;
    }

    @When("student-api-get-by-id is performed with student {string}")
    public void studentApiGetByIdIsPerformedWithStudent(String canonicalStudentId) {
        Student student = dataFixturesHolder.getStudents().get(canonicalStudentId);
        ResponseEntity<String> response = testRestTemplate().getForEntity(applicationUrl() + "/student/{id}", String.class, student.getId());
        studentApiHolder.setResponse(response);
    }

    @When("student-api-get-by-id is performed with a random id")
    public void studentApiGetByIdIsPerformedWithARandomId() {
        ResponseEntity<String> response = testRestTemplate().getForEntity(applicationUrl() + "/student/{id}", String.class, UUID.randomUUID());
        studentApiHolder.setResponse(response);
    }

    @Then("student-api-get-by-id returns a not_found response")
    public void studentApiGetByIdReturnsANot_foundResponse() {
        assertThat(studentApiHolder.getResponse().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Then("student-api-get-by-id returns a success response")
    public void studentApiGetByIdReturnsASuccessResponse() {
        assertThat(studentApiHolder.getResponse().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @And("student-api-get-by-id has details of student {string}")
    public void studentApiGetByIdHasDetailsOfStudent(String canonicalStudentId) {
        Student expectedStudent = dataFixturesHolder.getStudents().get(canonicalStudentId);
        StudentWithCourseResponseDto result = jsonConverter().fromJson(studentApiHolder.getResponse().getBody(), StudentWithCourseResponseDto.class);
        assertThat(result.getId()).isEqualTo(expectedStudent.getId());
        assertThat(result.getName()).isEqualTo(expectedStudent.getName());
        assertThat(result.getEmail()).isEqualTo(expectedStudent.getEmail());
    }

    @And("student-api-get-by-id has names of courses {string}")
    public void studentApiGetByIdHasNamesOfCourses(String canonicalCourseIds) {
        String[] expectedCourseNames = Arrays.stream(canonicalCourseIds.split(","))
                .map(s -> dataFixturesHolder.getCourses().get(s).getName())
                .toArray(String[]::new);
        StudentWithCourseResponseDto result = jsonConverter().fromJson(studentApiHolder.getResponse().getBody(), StudentWithCourseResponseDto.class);
        assertThat(result.getRegisteredCourses()).containsExactlyInAnyOrder(expectedCourseNames);
    }
}
