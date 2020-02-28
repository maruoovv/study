## 고급 맵핑

### 상속 관계 맵핑

관계형 데이터베이스에는 상속이라는 개념이 없다.  
대신 슈퍼타입-서브타입 관계라는 모델링 기법이 객체지향의 상속 개념과 가장 유사하다.
슈퍼타입-서브타입 논리 모델을 실제 물리 모델인 테이블로 구현할 때는 3가지 방법이 있다.

- 각각의 테이블로 변환 : 각각을 모두 테이블로 만들고 조회할 때 조인을 사용한다. JPA 에서는 조인 전략이라 한다.
- 통합 테이블로 변환 : 하나의 테이블만 사용한다. JPA 에서는 단일 테이블 전략이라 한다.
- 서브타입 테이블로 변환 : 서브 타입마다 하나의 테이블을 만든다. JPA 에서는 구현 클래스 마다 테이블 전략이라 한다.


#### 조인 전략 

조인전략은 엔티티 각각을 모두 테이블로 만들고 자식 테이블이 부모 테이블의 기본 키를 받아서 기본키+외래키로 사용하는 전략이다.  
따라서 조회할 때 조인을 자주 사용한다.  
객체는 타입으로 구분할 수 있지만 테이블은 타입의 개념이 없으므로, 타입을 구분하는 컬럼을 추가해야 한다.  

![JOINED_TABLE](https://user-images.githubusercontent.com/37106689/75433338-21c75e80-5993-11ea-984f-babdb61caf21.png)

```java
@Entity 
@Inheritance(strategy = InheritanceType.JOINED)
// 상속 맵핑은 부모 클래스에 @Inheritance 를 지정해야한다.
@DiscriminatorColumn(name = "DTYPE")
// 부모 클래스에 구분 컬럼을 지정한다. 이 컬럼으로 자식 테이블을 구분한다.
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;
    
    private String name;
    private String price;
}

@Entity 
@DiscriminatorValue("A")
public class Album extends Item {
    private String artist;
}

@Entity 
@DisciriminatorValue("M")
// 엔티티를 저장할 때 구분 컬럼에 입력할 값을 지정한다.
public class Movie extends Item {
    private String director;
    private String actor;
}
```

자식테이블은 부모 테이블의 ID 컬럼명을 그대로 사용하는데, 만약 변경하고 싶다면 @PrimaryKeyJoinColumn 을 사용한다.
```java
@Entity 
@DiscriminatorValue("B")
@PrimaryKeyJoinColumn(name = "BOOK_ID")
// Book 테이블의 ITEM_ID 기본 키 컬럼명을 BOOK_ID 로 변경
public class Book extends Item {
    private String author;
    private String isbn;
}
```

조인전략의 장점
 - 테이블이 정규화 된다.
 - 외래 키 참조 무결성 제약 조건을 활용할 수 있다.
 - 저장공간을 효율적으로 사용한다.
 
조인전략의 단점
 - 조회 시 조인이 많이 사용되므로 성능이 저하될 수 있다.
 - 조회 쿼리가 복잡하다.
 - 데이터를 등록할 때 INSERT query 를 두번 실행한다.
 
 
#### 단일 테이블 전략 
단일 테이블 전략은 테이블을 하나만 사용한다.  
그리고 구분 컬럼으로 어떤 자식 데이터가 저장되었는지 구분한다.

![SINGLE TABLE](https://user-images.githubusercontent.com/37106689/75443850-a02cfc00-59a5-11ea-834c-d1f0d514de37.png)

이 전략을 사용할 때 주의점은 자식 엔티티가 맵핑한 컬럼은 모두 null 을 허용해주어야 한다는 것이다.  
Book 엔티티만 저장할 경우, author, isbn 컬럼만 사용하고 다른 컬럼은 사용하지 않으므로 NULL 이 입력되기 때문이다.  


```java
@Entity 
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// 단일 테이블 전략 사용
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;
    private String name;
    private int price;
    private String artist;
    //...
} 

@Entity 
@DiscriminatorValue("A")
public class Album extends Item {
    //...
}

@Entity 
@DiscriminatorValue("M")
public class Movie extends Item {
    //...
}

@Entity 
@DiscriminatorValue("B")
public class Book extends Item {
    //...
}
```
단일 테이블 전략은 구분 컬럼(@DiscriminatorColumn)을 꼭 사용해야 한다.

장점 
- 조인이 필요 없으므로 일반적으로 조회 성능이 빠르다.
- 조회 쿼리가 단순하다.

단점
- 자식 엔티티가 맵핑한 컬럼은 모두 null 허용해야 한다.
- 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다. 그러므로 상황에 따라 조회 성능이 느려질 수 있다.


#### 구현 클래스마다 테이블전략

자신 엔티티마다 테이블을 만들고, 테이블 각각에 필요한 컬럼이 모두 있다.  

![TABLE_PER_CLASS](https://user-images.githubusercontent.com/37106689/75445455-c1dbb280-59a8-11ea-8dee-91bdf291329f.png)

```java
@Entity 
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
// 구현클래스마다 테이블 전략
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;
    
    private String name;
    private int price;
    //...
}

@Entity 
public class Album extends Item {
    // ...
}

@Entity 
public class Movie extends Item {
    // ...
}

@Entity 
public class Book extends Item {
    //...
}
```

이 전략은 자식 엔티티마다 테이블을 따로 만든다. 일반적으로 추천하지 않는다.  

장점
- 서브타입을 구분해서 처리할때 효과적이다.
- not null 제약조건 사용 가능

단점
- 여러 자식 테이블을 함께 조회할 때 성능이 느리다 (UNION 사용)
- 자식 테이블을 통합해서 쿼리하기 어렵다.


### @MappedSuperclass

부모 클래스는 테이블과 맵핑하지 않고 상속받는 자식 클래스에게 맵핑 정보만 제공하고 싶을 때 @MappedSuperclass 를 사용한다.

![MappedSuperclass](https://user-images.githubusercontent.com/37106689/75447992-bdfe5f00-59ad-11ea-873f-ddcc494cd697.png)


Member와 Seller 는 서로 관계과 없는 테이블이다.  
테이블의 연관관계는 없지만 공통 속성이 존재하므로, 공통 속성을 부모 클래스로 모으고 상속 관계로 만들 수 있다.

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;
}

@Entity 
public class Member extends BaseEntity {
    private String email;
}

@Entity 
public class Seller extends BaseEntity {
    private String shopName;
}
```

BaseEntity 에는 공통 속성을 정의하고, 자식 엔티티들은 상속을 통해 속성 정보를 물려 받았다.  
BaseEntity 는 테이블과 맵핑할 필요가 없다.  
부모로부터 물려받은 맵핑 정보를 재정의하려면 @AttributeOverrides 나 @AttributeOverride 를 사용하고,
연관관계를 재정의하려면 @AssociationOverrides 나 @AssociationOverride 를 사용한다.

```java
@Entity 
@AttributeOverride(name = "id", column = @Column(name = "MEMBER_ID"))
// 부모에게 상속받은 id 속성의 컬럼명을 MEMBER_ID 로 재정의
public class Member extends BaseEntity { 
    //...
}
```

```java
@Entity 
@AttributeOverrides({
     @AttributeOverride(name = "id", column = @Column(name = "MEMBER_ID"))
    ,@AttributeOverride(name = "name", column = @Column(name = "MEMBER_NAME")) 
})

// 부모에게 상속받은 id 속성의 컬럼명을 MEMBER_ID 로 재정의
// name 속성을 MEMBER_NAME 으로 재정의
public class Member extends BaseEntity { 
    //...
}
```

특징
- 테이블과 맵핑되지 않고 자식 클래스에 엔티티의 맵핑 정보를 상속하기 위해 사용
- @MappedSuperclass 로 지정된 클래스는 엔티티가 아니므로 em.find 나 JPQL 에서 사용할 수 없다.
- 이 클래스를 직접 생성하는 경우는 거의 없으므로 추상 클래스로 만드는 것을 권장.


### 복합 키와 식별 관계 맵핑

#### 식별관게 vs 비식별 관계

데이터베이스 테이블 사이에 관계는 외래 키가 기본 키에 포함되는지 여부에 따라 식별 관계와 비식별 관계로 구분한다.


#### 식별 관계 

식별 관계는 부모 테이블의 기본 키를 내려받아 자식 테이블의 기본키 + 외래 키로 사용하는 관계다.

![image](https://user-images.githubusercontent.com/37106689/75451966-bd1cfb80-59b4-11ea-893d-6bc735cb70f2.png)

CHILD 테이블은 PARENT 테이블의 기본 키를 받아 기본키 + 외래키로 사용한다.

#### 비식별 관계

비식별관계는 부모 테이블의 기본키를 받아서 자식 테이블의 외래 키로만 사용하는 관계다.

![image](https://user-images.githubusercontent.com/37106689/75452350-7da2df00-59b5-11ea-8808-0e8671248dc6.png)

비식별 관계는 외래 키에 NULL을 허용하는지에 따라 필수적 비식별 관계와 선택적 비식별 관계로 나눈다. 

- 필수적 비식별 관계(Mandatory) : 외래 키에 NULL을 허용하지 않는다. 연관관계를 필수로 맺어야 한다.
- 선택적 비식별 관계(Optional) : 외래 키에 NULL을 허용한다. 연관관계를 맺을지 말지 선택할 수 있다.

#### 복합 키 : 비식별 관계 맵핑

둘 이상의 컬럼으로 구성된 복합 키를 사용하려면 별도의 식별자 클래스를 만들어야 한다.  
JPA는 @IdClass, @EmbeddedId 2가지 방법을 제공한다.

**IdClass** 

![image](https://user-images.githubusercontent.com/37106689/75543877-24df4f00-5a66-11ea-8a4c-d69375eecf20.png)

parent - child 는 비식별 관계고 parent 는 복합 키를 사용한다.  
child 는 parent 의 pk 를 사용한다.

```java
@Entity 
@IdClass(ParentId.class)
// 식별자 클래스를 지정
public class Parent {
    @Id
    @Column(name = "PARENT_ID1")
    private String id1;
    
    @Id 
    @Column(name = "PARENT_ID2")
    private String id2;
}

public class ParentId implements Serializable {
    private String id1;
    private String id2;
    
    public ParentId() {
        
    }
    
    public ParentId(String id1, String id2) {
        this.id1 = id1;
        this.id2 = id2;
    }
    
    @Override
    public boolean equals(Object o) {
        //...
    }
    
    @Override
    public int hashCode() {
        // ...
    }
}
```

@IdClass 를 사용할 때 식별자 클래스는 다음 조건을 만족해야 한다.
- 식별자 클래스의 속성명과 엔티티에서 사용하는 식별자의 속성명이 같아야 한다.
    - 예제의 Parent.id1, Parent.id2 <-> ParentId.id1, ParentId.id2
- Serializable 을 구현해야 한다.
- equals, hashCode 를 구현해야 한다.
- 기본 생성자가 있어야 한다
- 식별자 클래스는 public 이어야 한다.

```java
public void test {
    Parent parent = new Parent();
    parent.setId1("id1");
    parent.setId2("id2");
    em.persist(parent);
    // 저장
    
    // 조회
    ParentId parentId = new ParentId("id1", "id2");
    Parent parent = em.find(Parent.class, parentId);
}
```

저장 코드를 보면 식별자 클래스를 지정하지 않는데, em.persist 호출 시 영속성 컨텍스트에 엔티티를 등록하기 직전에 식별자 클래스를 생성하고 키로 사용한다.
이제 자식 클래스를 추가해보자

```java
@Entity 
public class Child {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID1", referencedColumnName = "PARENT_ID1"),
            @JoinColumn(name = "PARENT_ID2", referencedColumnName = "PARENT_ID2")
    })
    private Parent parent;
}
```

부모 테이블의 기본 키가 복합 키이므로 자식 테이블의 외래 키도 복합 키다.  
따라서 외래 키 맵핑시 여러 컬럼을 맵핑해야 하므로 @JoinColumns 를 사용하여 각각 외래 키 컬럼을 맵핑한다.

**@EmbeddedId**

@IdClass 가 데이터베이스에 맞춘 방법이라면 @EmbeddedId 는 좀더 객체지향적인 방법이다.

```java
@Entity 
public class Parent {
    @EmbeddedId 
    private ParentId id;
}

@Embeddable
public class ParentId implements Serializable {
    @Column(name = "PARENT_ID1")
    private String id1;
    @Column(name = "PARENT_ID2")
    private String id2;
    
    // equals and hashcode..
}
```

@EmbeddedId 를 적용한 식별자 클래스는 다음 조건을 만족해야 한다.
- @Embeddable 어노테이션 추가
- Serializable 구현
- equals, hashcode 구현
- 기본 생성자가 있어야 한다.
- 식별자 클래스는 public 이어야 한다.

```java
public void test() {
    Parent parent = new Parent();
    ParentId parentId = new ParentId("id1", "id2");
    parent.setId(parentId);
    em.persist(parent);
    // 저장
    
    // 조회
    ParentId parentId = new ParentId("id1", "id2");
    Parent parent = em.find(Parent.class, parentId);
} 
```

** equals, hashcode 를 구현해야 하는 이유 **

영속성 컨텍스트는 엔티티의 식별자를 키로 사용하여 엔티티를 관리한다.  
그리고 식별자를 비교할 때 equals, hashcode 를 사용한다. 따라서 식별자 객체의 동등성(equals)이 지켜지지 않으면 
예상과 다른 엔티티가 조회되는 등 심각한 문제가 발생한다.


#### 복합 키 : 식별 관계 맵핑

![image](https://user-images.githubusercontent.com/37106689/75546795-c1a4eb00-5a6c-11ea-9006-9579731df35e.png)

부모-자식-손자 까지 기본키를 전달하는 식별 관계이다.  
식별 관게에서 자식 테이블은 부모 테이블의 기본 키를 포함해서 복합 키를 구성해야 한다.

**@IdClass**

```java
@Entity 
public class Parent {
    @Id @Column(name = "PARENT_ID")
    private String id;
    private String name;
}

@Entity 
@IdClass(ChildId.class)
public class Child {
    @Id
    @ManoToOne 
    @JoinColumn(name = "PARENT_ID")
    public Parent parent;
    
    @Id @Column(name = "CHILD_ID")
    private String childId;
    
    private String name;
}

@Embeddable
public class ChildId implements Serializable {
    private String parent;
    private String childId;
    // equals, hashcode..
}

@Entity 
@IdClass(GrandChildId.class)
public class GrandChild {
    @Id 
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID")
    })
    private Child child;
    
    @Id @Column(name = "GRANDCHILD_ID")
    private String id;
    
    private String name;
}

public class GrandChildId implements Serializable {
    private ChildId child;
    private String id;
    
    // equals, hashcode..
}

@Embeddable
public class GrandChildId implements Serializable {
    private ChildId childId; // GrandChild.child
    private String id; // GrandChild.id
    // equals, hashcode..
}
```

식별 관계는 기본 키와 외래 키를 같이 맵핑해야 한다.  
식별자 맵핑인 @Id 와 연관관계 맵핑인 @ManyToOne 을 같이 사용했다.

**@EmbeddedId**
@EmbeddedId 로 식별 관계를 구성할 때는 @MapsId 를 사용해야 한다.

```java
@Entity 
public class Parent {
    @Id @Column(name = "PARENT_ID")
    private String id;
    private String name;
}

@Entity 
public class Child {
    @EmbeddedId
    private ChildId id;
    
    @MapsId("parentId") // ChildId.parentId 맵핑
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    public Parent parent;
    
    private String name;
}

@Embeddable
public class ChildId implements Serializable {
    private String parentId; // @MapsId("parentId") 로 맵핑 
    
    @Column(name = "CHILD_ID")
    private String id;
    
    // equals, hashcode..
}

@Entity 
public class GrandChild {
    @EmbeddedId
    private GrandChildId id;
    
    @MapsId("childId") // GrandChildId.childId 맵핑
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID")
    })
    private Child child;
    
    private String name;
}

@Embedable
public class GrandChildId implements Serializable {
    private ChildId childId; // @MapsId("childId") 맵핑
    
    @Column(name = "GRANDCHILD_ID")
    private String id;
    
    // equals, hashcode..
}
```