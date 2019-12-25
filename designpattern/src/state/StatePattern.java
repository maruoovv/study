package state;

public class StatePattern {

    public static void main(String[] args) {
        SiegeTank siegeTank = new SiegeTank();

        siegeTank.attack();
        siegeTank.changeMode();
        siegeTank.attack();
        siegeTank.changeMode();
        siegeTank.attack();
    }
}
