package facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HotelService {
    private HashMap<String, List<Room>> hotels;

    public HotelService() {
        hotels = new HashMap<>();
    }

    public Room getAvailableRoom(Hotel hotel) {
        List<Room> rooms = hotels.getOrDefault(hotel.getRooms(), new ArrayList<>());

        List<Room> availableRooms = rooms.stream().filter(o -> o.isAvailable()).collect(Collectors.toList());
        if (availableRooms.isEmpty()) throw new RuntimeException("no available room");

        return availableRooms.get(0);
    }

    public void reserveRoom(Room room) {
        room.setAvailable(true);
    }
}
