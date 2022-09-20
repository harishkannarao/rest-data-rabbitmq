CREATE TABLE IF NOT EXISTS course
     (
            id         VARCHAR(36) NOT NULL,
            NAME       VARCHAR(255),
            rate       SMALLINT NOT NULL,
            workload   INT NOT NULL,
            teacher_id VARCHAR(36),
            PRIMARY KEY (id)
     );

CREATE TABLE IF NOT EXISTS teacher
     (
            id         VARCHAR(36) NOT NULL,
            email      VARCHAR(255),
            NAME       VARCHAR(255),
            pictureurl VARCHAR(255),
            PRIMARY KEY (id)
     );

CREATE TABLE IF NOT EXISTS student
    (
            id         VARCHAR(36) NOT NULL,
            email      VARCHAR(255),
            name       VARCHAR(255),
            PRIMARY KEY (id)
    );

ALTER TABLE course
  ADD CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id);

CREATE TABLE IF NOT EXISTS student_course
    (
            student_id  VARCHAR(36) NOT NULL,
            course_id   VARCHAR(36) NOT NULL,
            PRIMARY KEY (student_id, course_id)
    );

ALTER TABLE student_course
    ADD CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES student(id);

ALTER TABLE student_course
    ADD CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES course(id);