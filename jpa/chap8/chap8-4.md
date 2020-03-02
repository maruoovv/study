### 영속성 전이 
특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶으면 영속성 전이 기능을 사용하면 된다.  
JPA는 CASCADE 옵션으로 영속성 전이를 제공한다.

```JAVA
@Entity 
public class Parent {
    @Id @GeneratedValue
    private Long id;
    
    @OneToMany(mappedBy = "parent")
    private List<Child> children = new ArrayList<Child>();
}

@Entity 
public class Child {
    @id @GeneratedValue 
    private Long id;
    
    @ManyToOne
    private Parent parent;
}
```

다음과 같은 부모-자식 엔티티가 있을 때, 부모에게 2개의 자식을 저장 하려면 다음과 같이 했을 것이다.

```java
public void save() {
    // 부모 저장
    Parent parent = new parent();
    em.persist(parent);
    
    // 자식1 저장
    Child child1 = new Child();
    child1.setParent(parent);
    parent.getChildren().add(chlid1);
    em.persist(child1);
    
    // 자식2 저장
    Child child2 = new Child();
    child2.setParent(parent);
    parent.getChildren().add(child2);
    em.persist(child2):
}
```

JPA 에서 엔티티를 저장할 때 연관된 모든 엔티티는 영속 상태여야 한다.  
그래서 부모 엔티티, 자식 엔티티를 각각 영속 상태로 만들어 주었다.  
이 때 영속성 전이를 사용하면 부모만 영속 상태로 만들어도 연관된 자식 엔티티들 까지 한 번에 영속 상태로 만들 수 있다.

#### 영속성 전이 : 저장
CASCADE 옵션을 적용하면 영속성 전이를 활성화 시킬 수 있다.

```java
@Entity 
public class Parent {
    //...
    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    private List<Child> children = new ArrayList<>();
}
```

```java
public void save() {
    Child child1 = new Child();
    Child child2 = new Child();
    
    Parent parent = new Parent();
    child1.setParent(parent);
    child2.setParent(parent);
    parent.getChildren().add(child1);
    parent.getChildren().add(child2);
    
    em.persist(parent);
}
```

cascade 를 적용한 후에 parent 만 영속화 하면 자식들도 함께 영속화 된다.

#### 영속성 전이 : 삭제 

영속성 전이는 엔티티를 삭제할 때도 사용할 수 있다.  
CascaedType.REMOVE 로 설정하고 부모 엔티티만 삭제하면 연관된 자식 엔티티도 함께 삭제 된다.

#### CASCADE 종류 

cascade 엔 다양한 옵션이 있다.

```java
public enum CascadeType {
    ALL,
    PERSIST,
    MERGE,
    REMOVE,
    REFRESH,
    DETACH
}
```

여러 속성을 같이 사용할 수 있다.

#### 고아 객체 

JPA 는 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공하는데 이것을 고아 객체 제거라 한다.  
이 기능을 사용해서 부모 엔티티의 컬렉션에서 자식 엔티티의 참조만 제거하면 자식 엔티티가 자동으로 삭제된다.

```JAVA
@Entity 
public class Parent {
    @Id @GeneratedValue
    private Long id;
    
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    // 고아 객체 기능 활성화
    private List<Child> children = new ArrayList<>();
}
```

```java
Parent parent = em.find(Parent.class, "id");
parent.getChildren().remove(0); // 자식 엔티티를 컬렉션에서 제거
```

다음 코드 실행시, SQL DELETE 구문이 실행된다.  
고아 객체 제거는 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능이다.  
따라서 이 기능은 참조하는 곳이 하나일 때만 사용해야 한다. 