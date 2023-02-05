package nl.tudelft.sem.template.validator.enrollment;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EnrolledValidator extends BaseEnrollmentValidator {


    private final transient StudentRepository studentRepository;
    private final transient TeacherRepository teacherRepository;

    @Autowired
    public EnrolledValidator(StudentRepository studentRepository,
                             TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public void handle(int courseCode, String netID, HttpServletRequest request)
            throws ResponseStatusException {

        try {
            preExistingEnrolledCheck(courseCode, netID, request);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatus(), e.getReason());
        }

        super.checkNext(courseCode, netID, request);
    }


    private void preExistingEnrolledCheck(int courseCode, String netID, HttpServletRequest request)
            throws ResponseStatusException {
        if (request.getRequestURI().contains("addTeacher")
                || request.getRequestURI().contains("createCourse")) {
            if (teacherRepository.existsByNetIDAndCourseCode(netID, courseCode) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User already enrolled in this course");
            }
        } else if (request.getRequestURI().contains("enroll")) {
            if (studentRepository.existsByNetIDAndCourseCode(netID, courseCode) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User already enrolled in this course");
            }
        }




    }

}