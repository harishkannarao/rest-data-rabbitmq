package com.harishkannarao.restdatarabbitmq.steps;

import com.harishkannarao.restdatarabbitmq.dao.TeacherDAO;
import com.harishkannarao.restdatarabbitmq.entity.Teacher;
import com.harishkannarao.restdatarabbitmq.steps.holder.DataFixturesHolder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TeacherDaoSteps extends AbstractBaseSteps {

    private final DataFixturesHolder dataFixturesHolder;

    public TeacherDaoSteps(DataFixturesHolder dataFixturesHolder) {
        this.dataFixturesHolder = dataFixturesHolder;
    }

    @Then("I can find by id with {string} through teacher-dao")
    public void iCanFindByIdWithThroughTeacherDao(String canonicalTeacherName) {
        Teacher expectedTeacher = dataFixturesHolder.getTeachers().get(canonicalTeacherName);
        Optional<Teacher> actualTeacher = teacherDAO().findById(expectedTeacher.getId());
        assertThat(actualTeacher)
                .isNotEmpty()
                .hasValue(expectedTeacher);
    }

    @And("I can update {string} with name {string} through teacher-dao")
    public void iCanUpdateWithNameThroughTeacherDao(String canonicalTeacherName, String newTeacherName) {
        Teacher initialTeacher = dataFixturesHolder.getTeachers().get(canonicalTeacherName);
        Teacher updatedTeacher = initialTeacher.toBuilder()
                .name(newTeacherName)
                .build();
        teacherDAO().save(updatedTeacher);
        Optional<Teacher> result = teacherDAO().findById(updatedTeacher.getId());
        assertThat(result)
                .isNotEmpty()
                .hasValue(updatedTeacher);
        dataFixturesHolder.getTeachers().put(canonicalTeacherName, updatedTeacher);
    }

    @And("I can delete {string} through teacher-dao")
    public void iCanDeleteThroughTeacherDao(String canonicalTeacherName) {
        Teacher teacherToDelete = dataFixturesHolder.getTeachers().get(canonicalTeacherName);
        teacherDAO().deleteById(teacherToDelete.getId());
        Optional<Teacher> result = teacherDAO().findById(teacherToDelete.getId());
        assertThat(result).isEmpty();
        dataFixturesHolder.getTeachers().remove(canonicalTeacherName);
    }

    private TeacherDAO teacherDAO() {
        return getBean(TeacherDAO.class);
    }
}
