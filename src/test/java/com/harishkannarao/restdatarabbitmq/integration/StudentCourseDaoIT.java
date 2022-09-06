package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.dao.CourseDAO;
import com.harishkannarao.restdatarabbitmq.dao.StudentCourseDAO;
import com.harishkannarao.restdatarabbitmq.dao.StudentDAO;
import com.harishkannarao.restdatarabbitmq.dao.TeacherDAO;
import com.harishkannarao.restdatarabbitmq.entity.*;
import com.harishkannarao.restdatarabbitmq.fixtures.CourseFixtures;
import com.harishkannarao.restdatarabbitmq.fixtures.StudentFixtures;
import com.harishkannarao.restdatarabbitmq.fixtures.TeacherFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentCourseDaoIT extends AbstractBaseIT {

    @Test
    public void test_findByStudentAndCourseId_findByStudentId_findByCourseId() {
        Teacher teacher = TeacherFixtures.randomTeacher();
        teacherDAO().save(teacher);
        Course course1 = CourseFixtures.randomCourse(teacher.getId());
        Course course2 = CourseFixtures.randomCourse(teacher.getId());
        courseDAO().save(course1);
        courseDAO().save(course2);
        Student student1 = StudentFixtures.randomStudent();
        Student student2 = StudentFixtures.randomStudent();
        studentDAO().save(student1);
        studentDAO().save(student2);
        StudentCourse studentCourse1 = StudentCourse.builder().studentId(student1.getId()).courseId(course1.getId()).build();
        StudentCourse studentCourse2 = StudentCourse.builder().studentId(student2.getId()).courseId(course2.getId()).build();
        studentCourseDAO().save(studentCourse1);
        studentCourseDAO().save(studentCourse2);

        StudentCourse.StudentCourseId studentCourseId = StudentCourse.StudentCourseId.builder()
                .courseId(course1.getId())
                .studentId(student1.getId())
                .build();
        Optional<StudentCourse> byStudentAndCourseId = studentCourseDAO().findById(studentCourseId);
        assertThat(byStudentAndCourseId)
                .isNotEmpty()
                .hasValue(studentCourse1);

        List<StudentCourse> byStudentId = studentCourseDAO().findByStudentId(student1.getId());
        assertThat(byStudentId)
                .hasSize(1)
                .containsExactly(studentCourse1);

        List<StudentCourse> byCourseId = studentCourseDAO().findByCourseId(course2.getId());
        assertThat(byCourseId)
                .hasSize(1)
                .containsExactly(studentCourse2);
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
