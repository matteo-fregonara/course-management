package nl.tudelft.sem.template.validator.enrollment;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MaxCapacityValidator extends BaseEnrollmentValidator {

    private final transient StudentRepository studentRepository;
    private final transient CourseRepository courseRepository;


    @Autowired
    public MaxCapacityValidator(StudentRepository studentRepository,
                                CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void handle(int courseCode, String netID, HttpServletRequest request)
            throws ResponseStatusException {

        try {
            preExistingCapacityCheck(courseCode, netID);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getReason());
        }

        super.checkNext(courseCode, netID, request);
    }



    private void preExistingCapacityCheck(int courseCode, String netID)
            throws ResponseStatusException {
        Course course = courseRepository.findCourseByCourseCode(courseCode).get();
        int currentNumStudents = studentRepository.countAllByCourseCode(courseCode);
        if (currentNumStudents + 1 > course.getMaxSize()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Course has reached maximum number of students");
        }
    }
}
