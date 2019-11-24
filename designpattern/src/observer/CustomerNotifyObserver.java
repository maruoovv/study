package observer;

import java.util.List;

public class CustomerNotifyObserver implements Observer{
    private Subject waitingSystem;

    public CustomerNotifyObserver(Subject waitingSystem) {
        this.waitingSystem = waitingSystem;
        waitingSystem.registerObserver(this);
    }

    @Override
    public void update(List<Waiting> waiting) {
        waiting.forEach(
                o -> {
                    String phoneNumber = o.getCustomer().getPhoneNumber();
                    System.out.println("send to phoneNumber : " + phoneNumber + " remain time : " + o.getRemainTime());
                }
        );
    }
}
