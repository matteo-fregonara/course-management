package nl.tudelft.sem.template.strategy;

import java.util.List;
import nl.tudelft.sem.template.entities.Room;
import nl.tudelft.sem.template.repositories.RoomRepository;

public interface Strategy {
    public List<Room> getRooms(RoomRepository roomRepository);

    public List<Room> getRooms(RoomRepository roomRepository, Integer code);
}
