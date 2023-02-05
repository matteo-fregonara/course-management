package nl.tudelft.sem.template.validator.enrollment;

import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
import org.jetbrains.annotations.NotNull;

public class EnrollmentValidatorFactory {

    /**
     * Dispatcher for to get the corresponding validator.
     *
     * @param validatorType either "teacher" or "student"
     * @param courseRepository the course repository
     * @param studentRepository the student repository
     * @param teacherRepository the teacher repository
     * @return the dispatched validator
     */
    public static EnrollmentValidator getEnrollmentValidator(@NotNull String validatorType,
                                                             CourseRepository courseRepository,
                                                             StudentRepository studentRepository,
                                                             TeacherRepository teacherRepository) {
        switch (validatorType) {
            case "student":
                return BasicChain.getStudentEnrollmentValidator(
                    courseRepository, studentRepository, teacherRepository
                );
            case "teacher":
                return BasicChain.getTeacherEnrollmentValidator(
                    courseRepository, studentRepository, teacherRepository
                );
            default:
                throw new IllegalArgumentException();
        }
    }

}
