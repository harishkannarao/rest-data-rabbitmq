package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.dao.CourseDAO;
import com.harishkannarao.restdatarabbitmq.dao.StudentCourseDAO;
import com.harishkannarao.restdatarabbitmq.dao.StudentDAO;
import com.harishkannarao.restdatarabbitmq.dao.TeacherDAO;
import com.harishkannarao.restdatarabbitmq.entity.Course;
import com.harishkannarao.restdatarabbitmq.entity.Student;
import com.harishkannarao.restdatarabbitmq.entity.StudentCourse;
import com.harishkannarao.restdatarabbitmq.entity.Teacher;
import com.harishkannarao.restdatarabbitmq.fixtures.CourseFixtures;
import com.harishkannarao.restdatarabbitmq.fixtures.StudentFixtures;
import com.harishkannarao.restdatarabbitmq.fixtures.TeacherFixtures;
import com.harishkannarao.restdatarabbitmq.steps.holder.DataFixturesHolder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

public class DataFixturesSteps extends AbstractBaseSteps {

    private final DataFixturesHolder dataFixturesHolder;

    public DataFixturesSteps(DataFixturesHolder dataFixturesHolder) {
        this.dataFixturesHolder = dataFixturesHolder;
    }

    @Given("a random teacher known as {string}")
    public void aRandomTeacherKnownAs(String canonicalTeacherId) {
        Teacher teacher = TeacherFixtures.randomTeacher();
        teacherDAO().save(teacher);
        dataFixturesHolder.getTeachers().put(canonicalTeacherId, teacher);
    }

    @And("a random course known as {string} with teacher {string}")
    public void aRandomCourseKnownAsWithTeacher(String canonicalCourseId, String canonicalTeacherId) {
        Course course = CourseFixtures.randomCourse(dataFixturesHolder.getTeachers().get(canonicalTeacherId).getId());
        courseDAO().save(course);
        dataFixturesHolder.getCourses().put(canonicalCourseId, course);
    }

    @And("a random student known as {string}")
    public void aRandomStudentKnownAs(String canonicalStudentId) {
        Student student = StudentFixtures.randomStudent();
        studentDAO().save(student);
        dataFixturesHolder.getStudents().put(canonicalStudentId, student);
    }

    @And("student {string} is registered with course {string}")
    public void studentIsRegisteredWithCourse(String canonicalStudentId, String canonicalCourseId) {
        StudentCourse studentCourse = StudentCourse.builder()
                .studentId(dataFixturesHolder.getStudents().get(canonicalStudentId).getId())
                .courseId(dataFixturesHolder.getCourses().get(canonicalCourseId).getId())
                .build();
        studentCourseDAO().save(studentCourse);
    }

    private TeacherDAO teacherDAO() {
        return getBean(TeacherDAO.class);
    }

    private CourseDAO courseDAO() {
        return getBean(CourseDAO.class);
    }

    private StudentDAO studentDAO() {
        return getBean(StudentDAO.class);
    }
    private StudentCourseDAO studentCourseDAO() {
        return getBean(StudentCourseDAO.class);
    }
}
