package observer;

import java.time.Duration;
import java.util.List;

public class ObserverPattern {
    public static void main(String[] args) {
        WaitingSystem waitingSystem = new WaitingSystem();

        ElectricBoardObserver electricBoardObserver = new ElectricBoardObserver(waitingSystem);
        CustomerNotifyObserver customerNotifyObserver = new CustomerNotifyObserver(waitingSystem);

        Customer customer1 = new Customer("java", "12345678");
        Customer customer2 = new Customer("scala", "00000000");

        waitingSystem.registerWaiting(customer1);
        waitingSystem.registerWaiting(customer2);

        waitingSystem.reduceWaitingTimes();
        waitingSystem.reduceWaitingTimes();

        System.out.println("remove notify observer");
        waitingSystem.removeObserver(customerNotifyObserver);
        waitingSystem.reduceWaitingTimes();
    }
}

class Customer {
    private String name;
    private String phoneNumber;

    public Customer(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

class Waiting {
    private Customer customer;
    private Duration remainTime;

    public Waiting(Customer customer, Duration remainTime) {
        this.customer = customer;
        this.remainTime = remainTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Duration getRemainTime() {
        return remainTime;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setRemainTime(Duration remainTime) {
        this.remainTime = remainTime;
    }
}


interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObserver();
}

interface Observer {
    void update(List<Waiting> waiting);
}
