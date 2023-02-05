package nl.tudelft.sem.template.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import nl.tudelft.sem.template.communication.ServiceCommunication;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.repositories.CourseRepository;
import nl.tudelft.sem.template.validator.courses.CourseExistValidator;
import nl.tudelft.sem.template.validator.courses.CourseValidator;
import nl.tudelft.sem.template.validator.courses.RoomAvailabilityValidator;
import nl.tudelft.sem.template.validator.courses.RoomFitValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
@RequestMapping(path = "/courses")
public class CourseCreateController {

    @Autowired
    private transient CourseRepository courseRepository;


    @Autowired
    private transient EnrollmentController enrollmentController;

    private final transient String roomServiceUri = "http://localhost:8083/rooms";

    /**
     * Constructor that allow mocked instances of the class attributes to pass through.
     * This is an alternative to the `@InjectMock` annotation.
     *
     * @param courseRepository Mocked instance of CourseRepository
     */
    public CourseCreateController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * <p>Teacher creates a new course.</p>
     *
     * <p>Accept the post mapping on `/courses/createCourse` and parse the post request body and
     * create an instance of the new course.</p>
     *
     * @param newCourse Automatically retrieved argument by parsing the request body
     */
    @PostMapping(value = "/createCourse")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasRole('TEACHER')")
    public void createCourse(@Valid @RequestBody Course newCourse, HttpServletRequest request) {

        CourseValidator errHandler =
            new CourseExistValidator(courseRepository)
                .setNext(
                    new RoomAvailabilityValidator()
                        .setNext(
                            new RoomFitValidator()
                        )

                );

        errHandler.handle(newCourse, request);

        // Make a request to the room service to secure the room
        String url = roomServiceUri
                    + "/bookRoom/"
                    + newCourse.getRoomCode()
                    + "/"
                    + newCourse.getCourseCode();
        ServiceCommunication.sendGetRequest(url,
                request.getHeader("Authorization"),
                "Room not available", String.class);

        courseRepository.save(newCourse);

        String netID = UserInfoHelper.getNetID();

        enrollmentController.addTeacherToCourse(Integer.toString(newCourse.getCourseCode()),
                netID, request);
    }

}
