package observer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class WaitingSystem implements Subject {
    private List<Waiting> waitingList = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        int index = observers.indexOf(o);
        if (index >= 0) {
            observers.remove(index);
        }
    }

    @Override
    public void notifyObserver() {
        for (Observer observer : observers) {
            observer.update(waitingList);
        }
    }

    public void registerWaiting(Customer customer) {
        waitingList.add(new Waiting(customer, Duration.ofMinutes(20)));
    }

    public void reduceWaitingTimes() {
        waitingList.forEach(o -> o.setRemainTime(o.getRemainTime().minus(Duration.ofMinutes(1))));
        notifyObserver();
    }

}
