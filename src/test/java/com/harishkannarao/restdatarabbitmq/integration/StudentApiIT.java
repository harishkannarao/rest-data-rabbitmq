package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.dao.CourseDAO;
import com.harishkannarao.restdatarabbitmq.dao.StudentCourseDAO;
import com.harishkannarao.restdatarabbitmq.dao.StudentDAO;
import com.harishkannarao.restdatarabbitmq.dao.TeacherDAO;
import com.harishkannarao.restdatarabbitmq.dto.StudentWithCourseResponseDto;
import com.harishkannarao.restdatarabbitmq.entity.Course;
import com.harishkannarao.restdatarabbitmq.entity.Student;
import com.harishkannarao.restdatarabbitmq.entity.StudentCourse;
import com.harishkannarao.restdatarabbitmq.entity.Teacher;
import com.harishkannarao.restdatarabbitmq.fixtures.CourseFixtures;
import com.harishkannarao.restdatarabbitmq.fixtures.StudentFixtures;
import com.harishkannarao.restdatarabbitmq.fixtures.TeacherFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentApiIT extends AbstractBaseIT {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void getStudentDetailsById() {
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

        ResponseEntity<StudentWithCourseResponseDto> response = testRestTemplate().getForEntity(applicationUrl() + "/student/{id}", StudentWithCourseResponseDto.class, student1.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        StudentWithCourseResponseDto result = response.getBody();
        assertThat(result.getId()).isEqualTo(student1.getId());
        assertThat(result.getName()).isEqualTo(student1.getName());
        assertThat(result.getEmail()).isEqualTo(student1.getEmail());
        assertThat(result.getRegisteredCourses()).containsExactlyInAnyOrder(course1.getName());
    }

    @Test
    public void getStudentDetailsById_returns404() {
        ResponseEntity<StudentWithCourseResponseDto> response = testRestTemplate().getForEntity(applicationUrl() + "/student/{id}", StudentWithCourseResponseDto.class, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
