package nl.tudelft.sem.template.strategy;

import java.util.List;
import nl.tudelft.sem.template.entities.Room;
import nl.tudelft.sem.template.repositories.RoomRepository;

public class Rooms {
    private transient Strategy strategy;

    public Rooms(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<Room> getRooms(RoomRepository roomRepository) {
        return strategy.getRooms(roomRepository);
    }

    public List<Room> getRooms(RoomRepository roomRepository, Integer code) {
        return strategy.getRooms(roomRepository, code);
    }
}
