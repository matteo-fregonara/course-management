package nl.tudelft.sem.template.strategy;

import java.util.List;
import nl.tudelft.sem.template.entities.Room;
import nl.tudelft.sem.template.repositories.RoomRepository;

public class GetEmptyRooms implements Strategy {
    @Override
    public List<Room> getRooms(RoomRepository roomRepository) {
        return roomRepository.findEmptyRoom();
    }

    @Override
    public List<Room> getRooms(RoomRepository roomRepository, Integer code) {
        throw new IllegalArgumentException("Too many arguments");
    }
}
