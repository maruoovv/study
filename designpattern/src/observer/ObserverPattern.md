### 옵저버패턴

한 객체의 상태가 바뀌면 그 객체에 의존하는 다른 객체들한테 연락이 가고 자동으로 내용이 갱신되는 방식이다.  
일대다의 의존성을 가지고 있다.  

![img](https://user-images.githubusercontent.com/37106689/69149111-215c3380-0b19-11ea-8b9f-5c4a2193e5aa.JPG)

``` 
1. Subject
    - 주제를 나타내는 인터페이스다.
    - 주제를 구독하거나, 탈퇴하고 싶을때는 이 인터페이스의 메소드를 사용한다.
    - 주제 역할을 하는 구현 클래스는 이 인터페이스를 구현한다.

2. Observer
    - 각 주제에는 여러개의 옵저버가 있을 수 있다.
    - 옵저버가 될 객체는 반드시 Observer 인터페이스를 구현한다.
    - 주제 구체 클래스는 데이터 변경시 옵저버의 update 메소드를 호출한다, 
```
  
  
간단한 예제를 통해 옵저버 패턴을 적용해 보도록 하자.


#### 식당 대기 시스템 

식당 대기 시스템을 설계해보자.  
손님이 전화번호를 기록하면, 번호표가 나오고  
번호와 대기시간을 알려주는 전광판과 손님의 전화번호로 대기시간을 주기적으로 알려주는 시스템이 있다고 하자.
```java
class Customer {
    private String name;
    private String phoneNumber;
}

class Waiting {
    private Customer customer;
    private Duration remainTime;
}

interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObserver();
}

interface Observer {
    void update(List<Waiting> waiting);
}
```
도메인 객체와 옵저버 패턴을 위한 subject, observer 인터페이스를 작성했다.  
Subject 인터페이스를 구현하는 대기 시스템을 구현해보자.

```java
class WaitingSystem implements Subject {
    private List<Waiting> waitingList;
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
```

WaitingSystem 을 구독하는 옵저버들을 구현해보자.  
전광판 옵저버와, 연락처로 메시지를 보내주는 옵저버가 있다.

```java
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
                    System.out.println("send to phoneNumber : " + phoneNumber);
                }
        );
    }
}
```

예제를 실행해보자.
```java
class Example {
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

            // 연락처로 메시지를 보내주는 시스템에 문제가 생겨, 일시적으로 옵저버에서 탈퇴 하였다.              
           System.out.println("remove notify observer");
           waitingSystem.removeObserver(customerNotifyObserver);
           waitingSystem.reduceWaitingTimes();
    }
}
```

```
customer name : java remainTime : PT19M
customer name : scala remainTime : PT19M
send to phoneNumber : 12345678 remain time : PT19M
send to phoneNumber : 00000000 remain time : PT19M
customer name : java remainTime : PT18M
customer name : scala remainTime : PT18M
send to phoneNumber : 12345678 remain time : PT18M
send to phoneNumber : 00000000 remain time : PT18M
remove notify observer
customer name : java remainTime : PT17M
customer name : scala remainTime : PT17M
```

옵저버들은 Observer 라는 인터페이스를 구현하기만 하면,  
주제 객체는 옵저버들이 데이터를 어떤식으로 활용하는지 전혀 알 필요 없이,  
단지 데이터의 변경이 있다는 사실과, 변경된 데이터를 옵저버들에게 주기만 하면 된다.  
이러한 느슨한 결합을 통해, 옵저버가 추가되거나, 새로운 형식의 옵저버가 생기더라도 주제 객체에는 영향을 미치지 않는다.
