### 데코레이터 패턴 

객체에 추가 요소를 동적으로 더할 수 있다. 서브클래스들을 만드는 것보다 유연하게 기능을 확장할 수 있다.

![](https://user-images.githubusercontent.com/37106689/69240964-fb9a6180-0be0-11ea-999e-b9835034e9c8.JPG)

```
- ConcreteComponent 에 새로운 행동들을 동적으로 추가할 수 있다.
- 각 데코레이터 들은 자신이 장식할 구성요소(이 경우엔 ConcreteComponent) 와 같은 인터페이스 또는 추상클래스를 구현한다.
- 각 데코레이터들은 Component 객체를 가지고 있어 Component 의 행동을 확장할 수 있다.
```

어떤 컴포넌트에 여러가지 행동을 선택적으로 적용할 수 있을 때, 데코레이터 패턴을 사용할 수 있다.  
한 컴포넌트의 여러개의 데코레이터를 적용하여, 컴포넌트에 여러가지 행동을 추가해줄 수 있다.  
새로운 컴포넌트나 데코레이터가 추가 되었다고 해도, 기존의 코드를 수정할 필요 없이 새로운 요소들을 추가해주면 쉽게 확장이 가능하다.

데코레이터 패턴을 사용할 때 주의해야 할 점이 있는데, 각 데코레이터 요소들이 서로의 타입에 의존하는 코드를 짜면 안된다.  
또, 자잘한 객체들이 많이 추가될 수 있기 때문에 코드가 복잡해질 수 있는 위험이 있다.

간단한 예제를 통해 데코레이터 패턴을 적용해보자.

#### 피자가게
한 피자가게는 정해진 메뉴는 없고 원하는 도우와 토핑을 선택해야 한다.  
컴포넌트는 도우, 데코레이터는 토핑이 된다.

```java
interface Pizza {
    String name();
    int cost();
}

class ThinDough implements Pizza {
    @Override
    public String name() {
        return "Thin dough";
    }
    @Override
    public int cost() {
        return 10000;
    }
}

class OriginalDough implements Pizza {
    @Override
    public String name() {
        return "Original dough";
    }
    @Override
    public int cost() {
        return 12000;
    }
}

abstract class Ingredient implements Pizza {}
class Pepperoni extends Ingredient {
    private Pizza pizza;
    public Pepperoni(Pizza pizza) {
        this.pizza = pizza;
    }
    @Override
    public String name() {
        return pizza.name() + ", pepperoni";
    }
    @Override
    public int cost() {
        return pizza.cost() + 2000;
    }
}

class Cheese extends Ingredient {
    private Pizza pizza;
    public Cheese(Pizza pizza) {
        this.pizza = pizza;
    }
    @Override
    public String name() {
        return pizza.name() + ", cheese";
    }
    @Override
    public int cost() {
        return pizza.cost() + 2500;
    }
}

class Spinach extends Ingredient {
    private Pizza pizza;
    public Spinach(Pizza pizza) {
        this.pizza = pizza;
    }
    @Override
    public String name() {
        return pizza.name() + ", spinach";
    }
    @Override
    public int cost() {
        return pizza.cost() + 1000;
    }
}
```

이제 손님은 원하는 도우와 재료를 마음껏 선택할 수 있다.  
데코레이터 패턴을 적용했기 때문에 재료 추가는 제한없이 무한대로도 가능하다.
```java

class PizzaStore {
    public static void main(String[] args) {
        // 평범한 씬도우 피자
        Pizza pizza = new ThinDough();
        System.out.println(pizza.name() + ", cost : " + pizza.cost());

        // 페퍼로니, 치즈를 추가한 씬도우 피자
        Pizza pizza2 = new ThinDough();
        pizza2 = new Pepperoni(pizza2);
        pizza2 = new Cheese(pizza2);
        System.out.println(pizza2.name() + ", cost : " + pizza2.cost());

        // 시금치를 매우 좋아하는 사람이 주문한 오리지널 도우 피자
        Pizza pizza3 = new OriginalDough();
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        pizza3 = new Spinach(pizza3);
        System.out.println(pizza3.name() + ", cost : " + pizza3.cost());
    }
}
```

```
Thin dough, cost : 10000
Thin dough, pepperoni, cheese, cost : 14500
Original dough, spinach, spinach, spinach, spinach, spinach, spinach, spinach, spinach, spinach, spinach, spinach, spinach, cost : 24000
```