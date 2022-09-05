package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.dao.CourseDAO;
import com.harishkannarao.restdatarabbitmq.dao.TeacherDAO;
import com.harishkannarao.restdatarabbitmq.entity.Course;
import com.harishkannarao.restdatarabbitmq.entity.Teacher;
import com.harishkannarao.restdatarabbitmq.fixtures.CourseFixtures;
import com.harishkannarao.restdatarabbitmq.fixtures.TeacherFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CourseDAOIntegrationTest extends AbstractBaseIntegrationTest {

    @Test
    public void test_findByTeacherId() {
        Teacher teacher1 = TeacherFixtures.randomTeacher();
        Teacher teacher2 = TeacherFixtures.randomTeacher();
        teacherDAO().save(teacher1);
        teacherDAO().save(teacher2);
        Course course1 = CourseFixtures.randomCourse(teacher1);
        Course course2 = CourseFixtures.randomCourse(teacher2);
        courseDAO().save(course1);
        courseDAO().save(course2);

        List<Course> byTeacherId = courseDAO().findByTeacherId(teacher1.getId());
        assertThat(byTeacherId)
                .hasSize(1)
                .containsExactly(course1);
    }

    @Test
    public void test_findByTeacherName() {
        Teacher teacher1 = TeacherFixtures.randomTeacher();
        Teacher teacher2 = TeacherFixtures.randomTeacher();
        teacherDAO().save(teacher1);
        teacherDAO().save(teacher2);
        Course course1 = CourseFixtures.randomCourse(teacher1);
        Course course2 = CourseFixtures.randomCourse(teacher2);
        courseDAO().save(course1);
        courseDAO().save(course2);

        List<Course> byTeacherId = courseDAO().findByTeacherName(teacher1.getName());
        assertThat(byTeacherId)
                .hasSize(1)
                .containsExactly(course1);
    }

    private TeacherDAO teacherDAO() {
        return getBean(TeacherDAO.class);
    }

    private CourseDAO courseDAO() {
        return getBean(CourseDAO.class);
    }
}
