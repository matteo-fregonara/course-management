package nl.tudelft.sem.template.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "students_in_course")
public class Student {

    // PMD wants this for some reason
    private final transient String courseCodeColName = "course_code";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index", nullable = false)
    private int index;

    @Column(name = "netID", nullable = false)
    private String netID;

    @Column(name = "course_code", nullable = false)
    private int courseCode;



    /**
     * The constructor for student object.
     *
     * @param index the unique index of the student
     * @param netID the netID of the student
     * @param courseCode the course code the student is enrolled for
     */
    public Student(int index, String netID, int courseCode) {
        this.index = index;
        this.netID = netID;
        this.courseCode = courseCode;
    }

    /**
     * An alternative constructor so that a new student can get added to a course.
     * The index will be auto generated
     *
     * @param netID the netID of the student to be enrolled
     * @param courseCode the course to enroll the student in
     */
    public Student(String netID, int courseCode) {
        this.netID = netID;
        this.courseCode = courseCode;
    }

    public Student() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getNetID() {
        return netID;
    }

    public void setNetID(String netID) {
        this.netID = netID;
    }

    public int getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(int courseCode) {
        this.courseCode = courseCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return index == student.index
                && courseCode == student.courseCode
                && Objects.equals(netID, student.netID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, netID, courseCode);
    }
    
}
