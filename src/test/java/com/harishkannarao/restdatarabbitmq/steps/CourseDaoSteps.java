package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.dao.CourseDAO;
import com.harishkannarao.restdatarabbitmq.entity.Course;
import com.harishkannarao.restdatarabbitmq.entity.Teacher;
import com.harishkannarao.restdatarabbitmq.steps.holder.DataFixturesHolder;
import io.cucumber.java.en.Then;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CourseDaoSteps extends AbstractBaseSteps {

    private final DataFixturesHolder dataFixturesHolder;

    public CourseDaoSteps(DataFixturesHolder dataFixturesHolder) {
        this.dataFixturesHolder = dataFixturesHolder;
    }

    @Then("I can get courses {string} by teacher {string} through course-dao")
    public void iCanGetCoursesByTeacherThroughCourseDao(String canonicalCourseNames, String canonicalTeacherName) {
        Course[] expectedCourses = Arrays.stream(canonicalCourseNames.split(","))
                .map(s -> dataFixturesHolder.getCourses().get(s))
                .toArray(Course[]::new);
        Teacher teacher = dataFixturesHolder.getTeachers().get(canonicalTeacherName);
        List<Course> result = courseDAO().findByTeacherId(teacher.getId());
        assertThat(result).containsExactlyInAnyOrder(expectedCourses);
    }

    private CourseDAO courseDAO() {
        return getBean(CourseDAO.class);
    }
}
