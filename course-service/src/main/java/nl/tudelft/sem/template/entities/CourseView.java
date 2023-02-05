package nl.tudelft.sem.template.entities;

/**
 * This interface allows the student to have a limited overview of the course.
 * Missing administrative details such as maximum size as well as other students data
 */

public interface CourseView {

    String getUser_friendly_name();

    Integer getCourse_code();

    String getDescription();

    Integer getRoom_code();

}