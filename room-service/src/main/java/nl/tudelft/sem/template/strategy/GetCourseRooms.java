package nl.tudelft.sem.template.strategy;

import java.util.List;
import nl.tudelft.sem.template.entities.Room;
import nl.tudelft.sem.template.repositories.RoomRepository;

public class GetCourseRooms implements Strategy {
    @Override
    public List<Room> getRooms(RoomRepository roomRepository) {
        throw new IllegalArgumentException("Missing course code.");
    }

    @Override
    public List<Room> getRooms(RoomRepository roomRepository, Integer code) {
        return roomRepository.findByCourseCode(code);
    }
}

