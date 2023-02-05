package nl.tudelft.sem.template.controllers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.entities.Room;
import nl.tudelft.sem.template.repositories.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;


@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
//Duplicate literals suppressed as identical strings were used multiple times in URLs
public class RoomControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Mock
    private transient RoomRepository roomRepository;

    @InjectMocks
    private transient RoomController roomController;


    private transient Room unbookedRoom;
    private transient Room bookedRoom;
    private transient List<Room> rooms;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
        rooms = new ArrayList<>();
        unbookedRoom = new Room(0, "Room 0", 50, null);
        bookedRoom = new Room(1, "Room 1", 50, 1);
        rooms.add(unbookedRoom);
        rooms.add(bookedRoom);

        given(roomRepository.existsById(0)).willReturn(true);
        given(roomRepository.existsById(1)).willReturn(true);
    }

    @Test
    void testGetAllStudents() throws Exception {
        given(roomRepository.findAll()).willReturn(rooms);
        String result = "[{\"roomCode\":0,\"name\":\"Room 0\",\"maxSize\":50,\"courseCode\":null},"
                + "{\"roomCode\":1,\"name\":\"Room 1\",\"maxSize\":50,\"courseCode\":1}]";
        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/getAllRooms"))
                .andExpect(status().isOk())
                .andExpect(content().string(result));
    }

    @Test
    void testAvailableRoom() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(unbookedRoom);
        given(roomRepository.findByRoomCode(0)).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/available/0"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testAvailableInvalidRoom() throws Exception {
        given(roomRepository.findByRoomCode(50)).willThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room doesn't exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/available/50"))
                .andExpect(status().is(404));
    }

    @Test
    void testUnAvailableRoom() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(bookedRoom);
        given(roomRepository.findByRoomCode(1)).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/available/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testCheckFitForOversize() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(unbookedRoom);
        given(roomRepository.findByRoomCode(0)).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/checkFit/0/70"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testCheckFitForUnderSize() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(unbookedRoom);
        given(roomRepository.findByRoomCode(0)).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/checkFit/0/20"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testCheckFitForSameSize() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(unbookedRoom);
        given(roomRepository.findByRoomCode(0)).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/checkFit/0/50"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testCheckFitInvalidRoom() throws Exception {
        given(roomRepository.findByRoomCode(50)).willThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room doesn't exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/checkFit/50/500"))
                .andExpect(status().is(404));
    }

    @Test
    void testFindExistingCourse() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(bookedRoom);
        given(roomRepository.findByCourseCode(1)).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/findCourse/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "[{\"roomCode\":1,\"name\":\"Room 1\",\"maxSize\":50,\"courseCode\":1}]"));
    }

    @Test
    void testFindNonExistingCourse() throws Exception {
        List<Room> returnList = new ArrayList<>();
        given(roomRepository.findByCourseCode(8008)).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/findCourse/8008"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }


    @Test
    @WithMockUser("hasRole('TEACHER')")
    void testFindEmptyRooms() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(unbookedRoom);
        given(roomRepository.findEmptyRoom()).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/findEmptyRooms"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "[{\"roomCode\":0,\"name\":\"Room 0\","
                                + "\"maxSize\":50,\"courseCode\":null}]"));
    }

    @Test
    void testGetMaxCapacity() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(bookedRoom);
        given(roomRepository.findByRoomCode(1)).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/maxSize/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }

    @Test
    void testGetMaxCapacityInvalidRoom() throws Exception {
        given(roomRepository.findByRoomCode(50)).willThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room doesn't exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/maxSize/50"))
                .andExpect(status().is(404));
    }

    @Test
    @WithMockUser("hasRole('TEACHER')")
    void testBookRoom() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(unbookedRoom);
        given(roomRepository.findByRoomCode(0)).willReturn(returnList);

        Room updatedRoom = new Room(0, "Room 0", 50, 112);
        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/bookRoom/0/112"))
                .andExpect(status().isOk());

        verify(roomRepository, Mockito.times(1)).saveAndFlush(updatedRoom);
    }

    @Test
    @WithMockUser("hasRole('TEACHER')")
    void testBookInvalidRoom() throws Exception {
        given(roomRepository.findByRoomCode(50)).willThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room doesn't exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/bookRoom/50/512"))
                .andExpect(status().is(404));
    }

    @Test
    void testBookBookedRoom() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(bookedRoom);
        given(roomRepository.findByRoomCode(0)).willReturn(returnList);

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/bookRoom/0/112"))
                .andExpect(status().is(409));
    }

    @Test
    void testClearRoom() throws Exception {
        List<Room> returnList = new ArrayList<>();
        returnList.add(bookedRoom);
        given(roomRepository.findByRoomCode(1)).willReturn(returnList);

        Room updatedRoom = new Room(1, "Room 1", 50, null);
        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/clearRoom/1"))
                .andExpect(status().isOk());

        verify(roomRepository, Mockito.times(1)).saveAndFlush(updatedRoom);
    }

    @Test
    void testClearInvalidRoom() throws Exception {
        given(roomRepository.findByRoomCode(50)).willThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room doesn't exist"));

        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/clearRoom/50"))
                .andExpect(status().is(404));
    }
}
