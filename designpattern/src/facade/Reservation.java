package facade;

public class Reservation {
    private User user;
    private Hotel hotel;
    private boolean pickup;

    public User getUser() {
        return user;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public boolean isPickup() {
        return pickup;
    }
}
