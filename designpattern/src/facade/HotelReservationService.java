package facade;

public class HotelReservationService {
    private HotelService hotelService;
    private PickupService pickupService;

    public HotelReservationService(HotelService hotelService, PickupService pickupService) {
        this.hotelService = hotelService;
        this.pickupService = pickupService;
    }

    public void reserve(Reservation reservation) {
        try {
            Room availableRoom = hotelService.getAvailableRoom(reservation.getHotel());
            hotelService.reserveRoom(availableRoom);

            if (reservation.isPickup()) {
                pickupService.orderPickup();
            }

        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}
