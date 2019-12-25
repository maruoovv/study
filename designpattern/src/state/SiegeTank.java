package state;

public class SiegeTank {
    private SiegeTankMode mode;
    private SiegeTankMode siegeMode;
    private SiegeTankMode tankMode;

    public SiegeTank () {
        this.siegeMode = new SiegeMode();
        this.tankMode = new TankMode();
        this.mode = tankMode;
    }

    public void changeMode() {
        if (this.mode == tankMode) {
            this.mode = siegeMode;
        } else {
            this.mode = tankMode;
        }
    }

    public void attack() {
        this.mode.attack();
    }
}
