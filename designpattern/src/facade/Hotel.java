package facade;

import java.util.List;

public class Hotel {
    private String name;
    private String location;
    private List<Room> rooms;

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public List<Room> getRooms() {
        return rooms;
    }
}

class Room {
    private int roomNumber;
    private boolean available;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
