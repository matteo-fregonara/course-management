package nl.tudelft.sem.template.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "teachers_give_course")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index", nullable = false)
    private int index;


    @Column(name = "netID", nullable = false)
    private String netID;

    @Column(name = "course_code")
    private int courseCode;

    /**
     * The contructor for the Teacher object.
     *
     * @param index the unique index of the teacher
     * @param netID the netID of the teacher
     * @param courseCode the course code the teacher is teaching in
     */
    public Teacher(int index, String netID, int courseCode) {
        this.index = index;
        this.netID = netID;
        this.courseCode = courseCode;
    }

    /**
     * An alternative constructor so that a new teacher can get added to a course.
     * The index will be auto generated
     *
     * @param netID the netID of the teacher to be enrolled
     * @param courseCode the course to enroll the teacher in
     */
    public Teacher(String netID, int courseCode) {
        this.netID = netID;
        this.courseCode = courseCode;
    }

    public Teacher() {
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
        Teacher teacher = (Teacher) o;
        return index == teacher.index
                && courseCode == teacher.courseCode
                && Objects.equals(netID, teacher.netID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, netID, courseCode);
    }
}
