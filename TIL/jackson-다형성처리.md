Java 로 개발을 할때 우리는 다형성을 많이 활용한다.

다음과 같은 관계가 있다고 하자.

```java
public interface CellPhone {
    String getType();
}

public class IPhone implements CellPhone {
    private boolean canApplePay;

    @Override
    public String getType() {
        return "I-Phone";
    }
}

public class GalaxyPhone implements CellPhone {
    private boolean canSamsungPay;

    @Override
    public String getType() {
        return "Galaxy";
    }
}
```

위와 같은 관계가 있을때 우리는 여러 구현체를 하나의 컬렉션으로 묶어서 자주 사용한다

```java
List<CellPhone> phones = Arrays.asList(
                IPhone.builder().canApplePay(true).build(),
                IPhone.builder().canApplePay(false).build(),
                GalaxyPhone.builder().canSamsungPay(true).build(),
                GalaxyPhone.builder().canSamsungPay(false).build()
        );

System.out.println(phones);
```

이런 컬렉션을 JSON 형태로 serialize, deserialize 할 경우가 있다면 어떻게 할까?

java, spring 에서 주로 사용하는 Jackson 을 사용해보자

```java
List<CellPhone> phones = Arrays.asList(
                IPhone.builder().canApplePay(true).build(),
                IPhone.builder().canApplePay(false).build(),
                GalaxyPhone.builder().canSamsungPay(true).build(),
                GalaxyPhone.builder().canSamsungPay(false).build()
        );

ObjectMapper objectMapper = new ObjectMapper();

final String result = objectMapper.writeValueAsString(phones);
System.out.println(result);

[{"canApplePay":true,"type":"I-Phone"},{"canApplePay":false,"type":"I-Phone"},{"canSamsungPay":true,"type":"Galaxy"},{"canSamsungPay":false,"type":"Galaxy"}]
```

serialize 는 정상적으로 된다.

그렇다면 위의 serialize 된 string 을 동일하게 jackson 을 이용해 deserialize 해보자

```java
String str = "[{"canApplePay":true,"type":"I-Phone"},{"canApplePay":false,"type":"I-Phone"},{"canSamsungPay":true,"type":"Galaxy"},{"canSamsungPay":false,"type":"Galaxy"}]";

ObjectMapper objectMapper = new ObjectMapper();
final List<CellPhone> result = objectMapper.readValue(str, new TypeReference<List<CellPhone>>() {});

System.out.println(result);
```

위의 코드는 다음과 같은 에러를 발생시킨다.

```java
Cannot construct instance of `com.example.demo.CellPhone` (no Creators, like default constructor, exist): abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information
```

jackson 은 serialize 된 스트링이 각 구현체중 어떤것인지 알지 못해 에러를 발생시킨다.

이를 해결하려면 어떻게 해야할까?

Custom Deserializer 를 구현해 좀더 복잡하게 해결할수도 있겠지만, jackson 의 기능을 사용해보자

---

Jackson 의 `JsonTypeInfo` Annotation 을 활용해서 구현체의 구체 타입을 알려줄수 있다.

다음과 같이 상위 인터페이스에 선언해주자

```java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,include = JsonTypeInfo.As.PROPERTY,property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "Galaxy",value = GalaxyPhone.class),
@JsonSubTypes.Type(name = "I-Phone",value = IPhone.class)
})
public interface CellPhone {
    String getType();
}
```

JsonTypeInfo 를 이용하여 구체 클래스를 어떤식으로 결정할지 선언해줄수 있고,

JsonSubTypes 를 이용해 실체 구체 클래스들을 맵핑해준다.

이제 위의 deserialize 테스트를 다시 해보면 성공하는걸 볼수 있다!