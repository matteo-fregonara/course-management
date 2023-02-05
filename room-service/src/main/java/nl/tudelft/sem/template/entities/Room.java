package nl.tudelft.sem.template.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Room {

    @Id
    @Column(nullable = false, unique = true)
    private Integer roomCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer maxSize;

    @Column(unique = true)
    private Integer courseCode;

    protected Room(){

    }

    /**
     * Constructor for the room class.
     *
     * @param roomCode The room code of the room.
     * @param name The name of the room.
     * @param maxSize The maximum occupants the room can hold.
     * @param courseCode The course code of the course which is held in the room,
     *                   it is NULL when there is no course assigned to it.
     */
    public Room(Integer roomCode, String name, Integer maxSize, Integer courseCode) {
        this.roomCode = roomCode;
        this.name = name;
        this.maxSize = maxSize;
        this.courseCode = courseCode;
    }

    public Integer getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(Integer roomCode) {
        this.roomCode = roomCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Integer getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(Integer courseCode) {
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
        Room room = (Room) o;
        return roomCode.equals(room.roomCode)
            && name.equals(room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomCode, name);
    }
}
