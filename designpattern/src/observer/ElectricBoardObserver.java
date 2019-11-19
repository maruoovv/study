package observer;

import java.util.List;

public class ElectricBoardObserver implements Observer {
    private Subject waitingSystem;

    public ElectricBoardObserver(Subject waitingSystem) {
        this.waitingSystem = waitingSystem;
        waitingSystem.registerObserver(this);
    }

    @Override
    public void update(List<Waiting> waiting) {
        waiting.forEach(o -> System.out.println("customer name : " + o.getCustomer().getName() + " remainTime : " + o.getRemainTime()));
    }
}
