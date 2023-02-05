package nl.tudelft.sem.template.strategy;

import java.util.List;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;

public class CoursesOverview {
    private transient Strategy strategy;

    public CoursesOverview(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<Course> getCourses(String netID, CourseRepository courseRepository,
                                        StudentRepository studentRepository,
                                        TeacherRepository teacherRepository) {
        return strategy.getOverview(netID, courseRepository, studentRepository, teacherRepository);
    }
}
