### 스테이트 패턴

객체의 내부 상태가 바뀜에 따라서 객체의 행동을 바꿀 수 있다.

![img](https://user-images.githubusercontent.com/37106689/71361625-e39a7100-25d6-11ea-82c6-28f5137129f2.png)

```
Context : Context 클래스 안에는 여러 가지 내부 상태가 들어 있을 수 있다.
State : 구상 상태 클래스에 대한 공통 인터페이스를 정의한다.
State* : Context 로부터 전달된 요청을 실질적으로 처리한다. 
```
  
언뜻 보면 스트래티지 패턴과 유사해 보인다. 구조상으로는 스트래티지 패턴과 유사하지만,  
스테이트 패턴은 미리 정해진 규칙 기반으로 상태를 변경하고, 변경된 상태에 따라 다른 행동을 하게 할 수 있는 반면에  
스트래티지 패턴은 상태를 변경하는 것을 권장하는 편은 아니고, 변경하려면 외부에서 주입을 해주어야 한다.  


간단한 예제를 통해 스테이트 패턴을 적용해 보도록 하자.

---
스타크래프트 유닛중 시즈탱크는 시즈모드, 탱크모드를 변경하며 공격 할 수 있다.  
스테이트 패턴을 이용하여, 객체를 새로 생성하는 대신에 객체의 내부 상태를 변경하여 객체의 행동을 바꿀 수 있다.


```java
// 시즈탱크의 각 모드 상태를 구현
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

// 시즈탱크 객체는 각 상태를 갖고 있고, changeMode 호출시에 상태를 바꿈.
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

// 클라이언트에서는 상태를 바꾸는 메소드를 호출하는 것 만으로, 객체의 행동을 바꿀 수 있다.
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

```
