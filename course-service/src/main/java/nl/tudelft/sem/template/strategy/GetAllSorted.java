package nl.tudelft.sem.template.strategy;

import java.util.List;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;

public class GetAllSorted implements Strategy {
    @Override
    public List<Course> getOverview(String netID, CourseRepository courseRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository) {
        return courseRepository.getAllSorted();
    }
}
