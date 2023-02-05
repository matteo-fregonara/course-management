package nl.tudelft.sem.template.controllers;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.entities.Student;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.repositories.StudentRepository;
import nl.tudelft.sem.template.repositories.TeacherRepository;
import nl.tudelft.sem.template.strategy.CoursesOverview;
import nl.tudelft.sem.template.strategy.GetAll;
import nl.tudelft.sem.template.strategy.GetAllSorted;
import nl.tudelft.sem.template.strategy.GetInvolvedInStudent;
import nl.tudelft.sem.template.strategy.GetInvolvedInTeacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/courses")
public class CourseOverviewController {

    @Autowired
    private transient CourseRepository courseRepository;

    @Autowired
    private transient StudentRepository studentRepository;

    @Autowired
    private transient TeacherRepository teacherRepository;


    @GetMapping("/getAllStudents")
    @ResponseBody
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/{courseCode}") // Course information
    public @ResponseBody
    Optional<Course> getCourseByCourseCode(@PathVariable String courseCode) {
        return courseRepository.findCourseByCourseCode(Integer.parseInt(courseCode));
    }

    /** Endpoint to get all the courses a student is involved in.
     *
     * @param netID of the student
     * @returns list of all the courses a student is involved in
     */
    @GetMapping("/student/getCourses/{netID}")
    @ResponseBody
    public List<Course> getCoursesInvolvedInStudent(@PathVariable(value = "netID") String netID) {
        CoursesOverview coursesOverview = new CoursesOverview(new GetInvolvedInStudent());
        return coursesOverview.getCourses(netID, courseRepository,
                studentRepository, teacherRepository);
    }

    /** Endpoint to get all the courses a teacher is involved in.
     *
     * @param netID of the teacher
     * @returns list of all the courses a teacher is involved in
     */
    @GetMapping("/teacher/getCourses/{netID}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseBody
    public List<Course> getCoursesInvolvedInTeacher(@PathVariable(value = "netID") String netID) {
        CoursesOverview coursesOverview = new CoursesOverview(new GetInvolvedInTeacher());
        return coursesOverview.getCourses(netID, courseRepository,
                studentRepository, teacherRepository);
    }

    /** Endpoint to get all the courses (teacher only).
     *
     * @returns list of all the courses
     */
    @GetMapping("/teacher/getAll")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseBody
    public List<Course> getAllCoursesTeacher() {
        CoursesOverview coursesOverview = new CoursesOverview(new GetAll());
        return coursesOverview.getCourses("", courseRepository,
                studentRepository, teacherRepository);
    }

    /** Endpoint to get all the courses sorted on descending size (teacher only).
     *
     * @returns list of all the courses
     */
    @GetMapping("/teacher/getAllSorted")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseBody
    public List<Course> getAllCoursesSortedTeacher() {
        CoursesOverview coursesOverview = new CoursesOverview(new GetAllSorted());
        return coursesOverview.getCourses("", courseRepository,
                studentRepository, teacherRepository);
    }
}
