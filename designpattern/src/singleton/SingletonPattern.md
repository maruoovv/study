### 싱글턴 패턴

어떤 클래스의 인스턴스가 단 하나만 만들어지고, 어디서든지 그 인스턴스에 접근할수 있도록 하기 위한 패턴

---
#### 간단한 싱글턴 패턴

```java
public class Singleton {
    private static Singleton uniqueInstance;
    
    private Singleton() {}
    
    public static Singleton newInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Singleton();
        }
        
        return uniqueInstance;
    }
}
```

위 방법은 멀티스레드 환경에서 상당히 위험하다.  
한 스레드가 uniqueInstance 의 null 체크를 하고, 인스턴스 생성을 하기 이전에
다른 스레드가 null 검사 하는곳에 진입을 하게 된다면 인스턴스가 두개 이상 만들어질수 있다.  
이는 우리가 원하는 싱글턴 패턴이 아니다.
어떻게 문제를 해결할수 있을까?

```java
public class Singleton {
    private static Singleton uniqueInstance;
    
    private Singleton() {}
    
    public static synchronized Singleton newInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Singleton();
        }
        
        return uniqueInstance;
    }
}
```

위와 같이 호출 블럭을 동기화로 해결하거나,

```java
public class Singleton {
    private static Singleton uniqueInstance = new Singleton();
    
    private Singleton() {}
    
    public static synchronized Singleton newInstance() {
        return uniqueInstance;
    }
}
```

위와 같이 인스턴스를 처음부터 만들어 버린다.


---
코틀린이나 스칼라 같은 경우, 언어 자체에서 싱글턴 기능을 제공해준다.

```scala
object Blah {
  def sum(l: List[Int]): Int = l.sum
}
```

위 sum 메서드는 전역적으로 접근이 가능하고, 인스턴스는 하나만 생성된다.