package state;

public interface SiegeTankMode {
    void attack();
}

class SiegeMode implements SiegeTankMode {

    @Override
    public void attack() {
        System.out.println("Arclite Shock Cannon attack : damage 150");
    }
}

class TankMode implements SiegeTankMode {

    @Override
    public void attack() {
        System.out.println("Arclite Cannon attack : damage 30");
    }
}