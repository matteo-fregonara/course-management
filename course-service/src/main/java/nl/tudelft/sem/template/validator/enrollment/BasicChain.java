package nl.tudelft.sem.template.validator.enrollment;

import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;

public final class BasicChain {

    /**
     * returns the teacher chain of responsibility.
     *
     * @return a validator for teacher enrollment
     */
    public static EnrollmentValidator getTeacherEnrollmentValidator(
        CourseRepository courseRepository,
        StudentRepository studentRepository,
        TeacherRepository teacherRepository) {
        return new CourseExistsValidator(courseRepository)
            .setNext(
                new EnrolledValidator(studentRepository, teacherRepository)
                    .setNext(
                        new RoleCheckValidator()
                    )
            );
    }

    /**
     * returns the student chain of responsibility.
     *
     * @return a validator for student enrollment
     */
    public static EnrollmentValidator getStudentEnrollmentValidator(
        CourseRepository courseRepository,
        StudentRepository studentRepository,
        TeacherRepository teacherRepository) {
        return new CourseExistsValidator(courseRepository)
                .setNext(
                    new OpenEnrollmentValidator(courseRepository)
                        .setNext(
                            new EnrolledValidator(studentRepository, teacherRepository)
                                .setNext(
                                    new MaxCapacityValidator(studentRepository,
                                        courseRepository)
                                        .setNext(
                                            new RoleCheckValidator()
                                        )
                                )
                        )
                );
    }




}
