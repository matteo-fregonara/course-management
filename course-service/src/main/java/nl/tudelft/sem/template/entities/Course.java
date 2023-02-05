package nl.tudelft.sem.template.entities;

import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "courses")
public class Course {

    // PMD wants this for some reason
    private final transient String courseCodeColName = "course_code";

    @Id
    @NotNull
    @Column(name = "course_code", nullable = false)
    private Integer courseCode;

    @NotNull
    @Column(name = "max_size", nullable = false)
    private Integer maxSize;

    @Column(name = "user_friendly_name")
    private String courseName;

    @Column(name = "description")
    private String description;

    @Column(name = "can_enroll")
    private boolean canEnroll;

    @NotNull
    @Column(name = "room_code", nullable = false)
    private Integer roomCode;

    @OneToMany(targetEntity = Student.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = courseCodeColName, referencedColumnName = courseCodeColName)
    private List<Student> student;


    @OneToMany(targetEntity = Teacher.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = courseCodeColName, referencedColumnName = courseCodeColName)
    private List<Teacher> teacher;

    /**
     * Course constructor.
     *
     * @param courseCode the unique code of the course
     * @param maxSize the maximum size
     * @param courseName the human friendly name of the course
     * @param description the description of a course
     * @param canEnroll boolean describing whether the curse is open for enrollement
     * @param roomCode the unique code of the room that the course is allocated to
     * @param student list of students in the course
     * @param teacher list of teachers in the course
     */
    public Course(int courseCode, int maxSize, String courseName, String description,
                  boolean canEnroll, int roomCode, List<Student> student, List<Teacher> teacher) {
        this.courseCode = courseCode;
        this.maxSize = maxSize;
        this.courseName = courseName;
        this.description = description;
        this.canEnroll = canEnroll;
        this.roomCode = roomCode;
        this.student = student;
        this.teacher = teacher;
    }

    public Course() {
    }




    public List<Student> getStudent() {
        return student;
    }

    public void setStudent(List<Student> student) {
        this.student = student;
    }

    public List<Teacher> getTeacher() {
        return teacher;
    }

    public void setTeacher(List<Teacher> teacher) {
        this.teacher = teacher;
    }

    public int getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(int courseCode) {
        this.courseCode = courseCode;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCanEnroll() {
        return canEnroll;
    }

    public void setCanEnroll(boolean canEnroll) {
        this.canEnroll = canEnroll;
    }

    public int getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(int roomCode) {
        this.roomCode = roomCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course course = (Course) o;
        return courseCode.equals(course.courseCode)
                && maxSize.equals(course.maxSize)
                && canEnroll == course.canEnroll
                && roomCode.equals(course.roomCode)
                && Objects.equals(courseName, course.courseName)
                && Objects.equals(description, course.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode, maxSize, courseName, description, canEnroll, roomCode);
    }

}
