CREATE TABLE IF NOT EXISTS course
     (
            id         UUID NOT NULL,
            NAME       VARCHAR(255),
            rate       INT2 NOT NULL,
            workload   INT4 NOT NULL,
            teacher_id UUID,
            PRIMARY KEY (id)
     );

CREATE TABLE IF NOT EXISTS teacher
     (
            id         UUID NOT NULL,
            email      VARCHAR(255),
            NAME       VARCHAR(255),
            pictureurl VARCHAR(255),
            PRIMARY KEY (id)
     );

CREATE TABLE IF NOT EXISTS student
    (
            id         UUID NOT NULL,
            email      VARCHAR(255),
            name       VARCHAR(255),
            PRIMARY KEY (id)
    );

ALTER TABLE course
  ADD CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id);

CREATE TABLE IF NOT EXISTS student_course
    (
            student_id  UUID NOT NULL,
            course_id   UUID NOT NULL,
            PRIMARY KEY (student_id, course_id)
    );

ALTER TABLE student_course
    ADD CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES student(id);

ALTER TABLE student_course
    ADD CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES course(id);