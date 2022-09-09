package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.dao.StudentCourseDAO;
import com.harishkannarao.restdatarabbitmq.entity.Course;
import com.harishkannarao.restdatarabbitmq.entity.Student;
import com.harishkannarao.restdatarabbitmq.entity.StudentCourse;
import com.harishkannarao.restdatarabbitmq.steps.holder.DataFixturesHolder;
import io.cucumber.java.en.Then;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentCourseDao extends AbstractBaseSteps {

    private final DataFixturesHolder dataFixturesHolder;

    public StudentCourseDao(DataFixturesHolder dataFixturesHolder) {
        this.dataFixturesHolder = dataFixturesHolder;
    }

    @Then("I can lookup students {string} by course {string} through student-course-dao")
    public void iCanLookupStudentByCourseThroughStudentCourseDao(String canonicalStudents, String canonicalCourse) {
        Course course = dataFixturesHolder.getCourses().get(canonicalCourse);
        StudentCourse[] expectedStudentCourses = Arrays.stream(canonicalStudents.split(","))
                .map(s -> dataFixturesHolder.getStudents().get(s))
                .map(student -> StudentCourse.builder().courseId(course.getId()).studentId(student.getId()).build())
                .toArray(StudentCourse[]::new);
        List<StudentCourse> result = studentCourseDAO().findByCourseId(course.getId());

        assertThat(result).containsExactlyInAnyOrder(expectedStudentCourses);
    }

    @Then("I can lookup courses {string} by student {string} through student-course-dao")
    public void iCanLookupCourseByStudentThroughStudentCourseDao(String canonicalCourses, String canonicalStudent) {
        Student student = dataFixturesHolder.getStudents().get(canonicalStudent);
        StudentCourse[] expectedStudentCourses = Arrays.stream(canonicalCourses.split(","))
                .map(s -> dataFixturesHolder.getCourses().get(s))
                .map(course -> StudentCourse.builder().studentId(student.getId()).courseId(course.getId()).build())
                .toArray(StudentCourse[]::new);
        List<StudentCourse> result = studentCourseDAO().findByStudentId(student.getId());

        assertThat(result).containsExactlyInAnyOrder(expectedStudentCourses);
    }

    private StudentCourseDAO studentCourseDAO() {
        return getBean(StudentCourseDAO.class);
    }
}
