package nl.tudelft.sem.template.repositories;

import java.util.List;
import nl.tudelft.sem.template.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query(nativeQuery = true,
        value = "SELECT * FROM room "
              + "WHERE room_code = :room_code")
    List<Room> findByRoomCode(@Param("room_code") Integer roomCode);

    @Query(nativeQuery = true,
            value = "SELECT * FROM room "
                  + "WHERE course_code = :course_code")
    List<Room> findByCourseCode(@Param("course_code") Integer courseCode);

    @Query(nativeQuery = true,
            value = "SELECT * FROM room "
                  + "WHERE course_code IS NULL")
    List<Room> findEmptyRoom();
}
