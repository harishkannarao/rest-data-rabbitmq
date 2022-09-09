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

    @When("I perform student-api-get-by-id with ID of student {string}")
    public void iPerformStudentApiGetByIdWithIDOfStudent(String canonicalStudentId) {
        Student student = dataFixturesHolder.getStudents().get(canonicalStudentId);
        ResponseEntity<String> response = testRestTemplate().getForEntity(applicationUrl() + "/student/{id}", String.class, student.getId());
        studentApiHolder.setGetByIdResponse(response);
    }

    @When("I perform student-api-get-by-id with a random id")
    public void iPerformStudentApiGetByIdWithARandomId() {
        ResponseEntity<String> response = testRestTemplate().getForEntity(applicationUrl() + "/student/{id}", String.class, UUID.randomUUID());
        studentApiHolder.setGetByIdResponse(response);
    }

    @Then("I get a not_found response from student-api-get-by-id")
    public void iGetANot_foundResponseFromStudentApiGetById() {
        assertThat(studentApiHolder.getGetByIdResponse().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Then("I get a success response from student-api-get-by-id")
    public void iGetASuccessResponseFromStudentApiGetById() {
        assertThat(studentApiHolder.getGetByIdResponse().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @And("I see the details of student {string} from student-api-get-by-id")
    public void iSeeTheDetailsOfStudentFromStudentApiGetById(String canonicalStudentId) {
        Student expectedStudent = dataFixturesHolder.getStudents().get(canonicalStudentId);
        StudentWithCourseResponseDto result = extractStudentWithCourseResponseDto();
        assertThat(result.getId()).isEqualTo(expectedStudent.getId());
        assertThat(result.getName()).isEqualTo(expectedStudent.getName());
        assertThat(result.getEmail()).isEqualTo(expectedStudent.getEmail());
    }

    @And("I see the names of courses {string} from student-api-get-by-id")
    public void iSeeTheNamesOfCoursesFromStudentApiGetById(String canonicalCourseIds) {
        String[] expectedCourseNames = Arrays.stream(canonicalCourseIds.split(","))
                .map(s -> dataFixturesHolder.getCourses().get(s).getName())
                .toArray(String[]::new);
        StudentWithCourseResponseDto result = extractStudentWithCourseResponseDto();
        assertThat(result.getRegisteredCourses()).containsExactlyInAnyOrder(expectedCourseNames);
    }

    private StudentWithCourseResponseDto extractStudentWithCourseResponseDto() {
        return jsonConverter().fromJson(studentApiHolder.getGetByIdResponse().getBody(), StudentWithCourseResponseDto.class);
    }
}
