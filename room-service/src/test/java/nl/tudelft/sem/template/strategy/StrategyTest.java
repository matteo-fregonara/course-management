package nl.tudelft.sem.template.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.entities.Room;
import nl.tudelft.sem.template.repositories.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class StrategyTest {

    @Mock
    private transient RoomRepository roomRepository;

    private transient Room unbookedRoom;
    private transient Room bookedRoom;

    @BeforeEach
    void setUp() {
        unbookedRoom = new Room(0, "Room 0", 50, null);
        bookedRoom = new Room(1, "Room 1", 50, 1);
    }


    @Test
    public void getAllRooms() {

        List<Room> roomList = new ArrayList<>();
        roomList.add(unbookedRoom);
        roomList.add(bookedRoom);

        System.out.println(roomRepository);
        given(roomRepository.findAll()).willReturn(roomList);

        Rooms rooms = new Rooms(new GetAllRooms());
        assertEquals(roomList, rooms.getRooms(roomRepository));
    }

    @Test
    public void getAllRoomsMultipleArgument() {

        Rooms rooms = new Rooms(new GetAllRooms());
        assertThrows(IllegalArgumentException.class,
            () -> {
                rooms.getRooms(roomRepository, 52);
            });
    }

    @Test
    public void getEmptyRooms() {

        List<Room> roomList = new ArrayList<>();
        roomList.add(unbookedRoom);

        System.out.println(roomRepository);
        given(roomRepository.findEmptyRoom()).willReturn(roomList);

        Rooms rooms = new Rooms(new GetEmptyRooms());
        assertEquals(roomList, rooms.getRooms(roomRepository));
    }

    @Test
    public void getEmptyRoomsMultipleArgument() {

        Rooms rooms = new Rooms(new GetEmptyRooms());
        assertThrows(IllegalArgumentException.class,
            () -> {
                rooms.getRooms(roomRepository, 52);
            });
    }

    @Test
    public void getRoomWithRoomCode() {

        List<Room> roomList = new ArrayList<>();
        roomList.add(unbookedRoom);

        System.out.println(roomRepository);
        given(roomRepository.findByRoomCode(0)).willReturn(roomList);

        Rooms rooms = new Rooms(new GetRoom());
        assertEquals(roomList, rooms.getRooms(roomRepository, 0));
    }

    @Test
    public void getRoomWithRoomCodeSingleArgument() {

        Rooms rooms = new Rooms(new GetRoom());
        assertThrows(IllegalArgumentException.class,
            () -> {
                rooms.getRooms(roomRepository);
            });
    }

    @Test
    public void getRoomWithCourseCode() {

        List<Room> roomList = new ArrayList<>();
        roomList.add(bookedRoom);

        System.out.println(roomRepository);
        given(roomRepository.findByCourseCode(1)).willReturn(roomList);

        Rooms rooms = new Rooms(new GetCourseRooms());
        assertEquals(roomList, rooms.getRooms(roomRepository, 1));
    }

    @Test
    public void getRoomWithCourseCodeSingleArgument() {

        Rooms rooms = new Rooms(new GetCourseRooms());
        assertThrows(IllegalArgumentException.class,
            () -> {
                rooms.getRooms(roomRepository);
            });
    }

}
