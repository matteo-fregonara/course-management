package nl.tudelft.sem.template.strategy;

import java.util.List;
import nl.tudelft.sem.template.entities.Room;
import nl.tudelft.sem.template.repositories.RoomRepository;

public class GetRoom implements Strategy {
    @Override
    public List<Room> getRooms(RoomRepository roomRepository) {
        throw new IllegalArgumentException("Missing room code.");
    }

    @Override
    public List<Room> getRooms(RoomRepository roomRepository, Integer code) {
        return roomRepository.findByRoomCode(code);
    }
}