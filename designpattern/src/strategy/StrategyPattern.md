### 스트래티지 패턴 

알고리즘군을 정의하고 각각을 캡슐화하여 사용할수 있도록 한다.  
간단한 예제를 통해 스트래티지 패턴을 적용해 보기로 하자.

#### 결제 시스템 

결제 시스템을 설계해보자.
장바구니에 아이템을 담고, 결제를 할수 있다.
초기에는 현금(원) 결제만 가능했다.

```java
class Item {
    int price;
}

class Cart {
    List<Item> items;
    
    private int calculateTotalFee() {
        return items.stream().mapToInt(o -> o.getPrice()).sum();
    }
    
    public int buy(int pay) {
        int sum = calculateTotalFee(); 
        
        if (pay < sum) throw RuntimeException("요금이 부족합니다.");
        
        return pay - sum;
    }
}
```

외국인의 방문이 많아져 엔화, 달러화도 결제가 가능하게 바뀌어야 한다.
환율 계산을 하는 내부 메소드를 적용한다.

```java
enum PayType {
    DOLLAR, YEN, WON
}

class Item {
    int price;
}

class Cart {
    List<Item> items;
    
    private int calculateTotalFee() {
        return items.stream().mapToInt(o -> o.getPrice()).sum();
    }
    
    private int calculateExchangeFee(PayType payType, int pay) {
        switch(payType) {
            case WON:
                return pay;
            case DOLLAR:
                return pay * 1165;
            case YEN:
                return pay * 10;
        }
    }
    
    public int buy(int pay, PayType payType) {
        int sum = calculateTotalFee(); 
        
        double exchangedFee = calculateExchangeFee(payType, pay);
        if (exchangedFee < sum) throw RuntimeException("요금이 부족합니다.");
        
        return pay - sum;
    }
}
```

이정도도 괜찮다고 생각할수 있지만..  
환율이 바뀔때마다, 새로운 화폐가 추가 될때마다 calculateExchangeFee 메소드는 변경되어야 하고,  
이는 Cart 도메인 객체가 변경되어야 함을 뜻한다.  

```java
interface PayType {
    int calculateExchangeFee(int pay);
}

class WonPayType implements PayType {
    int exchangeRate = 1;
    
    @Override
    int calculateExchangeFee(int pay) {
        return pay * exchangeRate;
    }
}

class YenPayType implements PayType {
    int exchangeRate = 10;
    
    @Override
    int calculateExchangeFee(int pay) {
        return pay * exchangeRate;
    }
}

class DollarPayType implements PayType {
    int exchangeRate = 1165;
        
    @Override
    int calculateExchangeFee(int pay) {
        return pay * exchangeRate;
    }
}

class Item {
    int price;
}

class Cart {
    List<Item> items;
    
    private int calculateTotalFee() {
        return items.stream().mapToInt(o -> o.getPrice()).sum();
    }
    
    public int buy(int pay, PayType payType) {
        int sum = calculateTotalFee(); 
        
        double exchangedFee = payType.calculateExchangeFee(pay);
        if (exchangedFee < sum) throw new RuntimeException("요금이 부족합니다.");
        
        return pay - sum;
    }
}
```

이런식으로 구현을 하고 클라이언트에서 어떤 payType 을 사용할지 지정해준다면  
새로운 PayType 이 추가되거나 환율이 변경된다고 하더라도 Cart 도메인 객체는 변경으로부터 안전하다.  

**달라지는 부분을 찾아내고, 달라지지 않는 부분으로부터 분리한다.** 라는 디자인 원칙 1번을 사용하여,  
달라지는 부분인 환율과, 추가될수 있는 PayType 을 스트래티지 패턴을 이용하여 분리하였다.  
새로운 PayType 인 유로화가 추가된다 하더라도, 단순히 PayType 을 구현하는 구현체를 만들고, 클라이언트에서 사용하게 하면 된다.  
클라이언트는 동적으로 전략을 선택할 수 있는 장점도 있다.
