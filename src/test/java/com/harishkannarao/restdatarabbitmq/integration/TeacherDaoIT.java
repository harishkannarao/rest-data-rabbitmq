package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.dao.TeacherDAO;
import com.harishkannarao.restdatarabbitmq.entity.Teacher;
import com.harishkannarao.restdatarabbitmq.fixtures.TeacherFixtures;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TeacherDaoIT extends AbstractBaseIT {

    @Test
    public void test_CRUD() {
        Teacher input = TeacherFixtures.randomTeacher();
        teacherDAO().save(input);

        Teacher readResult = teacherDAO().findById(input.getId()).orElseThrow();
        assertThat(readResult).isEqualTo(input);

        Teacher updatedInput = input.toBuilder().name("some other name").build();
        teacherDAO().save(updatedInput);

        Teacher updatedReadResult = teacherDAO().findById(input.getId()).orElseThrow();
        assertThat(updatedReadResult).isEqualTo(updatedInput);

        teacherDAO().deleteById(input.getId());
        Optional<Teacher> deleteResult = teacherDAO().findById(input.getId());
        assertThat(deleteResult).isEmpty();
    }

    private TeacherDAO teacherDAO() {
        return getBean(TeacherDAO.class);
    }
}
