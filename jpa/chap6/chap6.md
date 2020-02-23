## 다양한 연관관계 맵핑

엔티티의 연관관계를 맵핑할 때는 3가지를 고려해야 한다.  
- 다중성
    - 다대일 (@ManyToOne)
    - 일대다 (@OneToMany)
    - 일대일 (@OneToOne)
    - 다대다 (@ManyToMany)
- 단방향, 양방향
- 연관관계의 주인

### 다대일 

다대일 관계의 반대 방향은 항상 일대다 관계이고, 일대다 관계의 반대 방향은 항상 다대일 관계다.  
데이터베이스 테이블의 일대다 관계에서 외래 키는 항상 다 쪽에 있다.  
따라서 객체 관계에서 연관관계의 주인은 항상 다쪽이다.  

#### 다대일 단방향 

```java
@Entity
public class Member {
    @Id 
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
}

@Entity
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    
    private String name;
}
```

회원은 Member.team 으로 팀 엔티티를 참조하지만 반대인 Team 에는 회원을 참조하는 필드가 없다.  
따라서 회원과 팀은 다대일 단방향 관게이다.

#### 다대일 양방향 

```java
@Entity
public class Member {
    @Id 
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    
    private String username;
    
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    
    public void setTeam(Team team) {
        this.team = team;
        
        if (!team.getMembers().contains(this)) {
            team.getMembers().add(this);
        }
    }
}

@Entity
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();
    
    public void addMember(Member member) {
        this.members.add(member);
        
        if (member.getTeam() != this) {
            member.setTeam(this);
        }
    }
}
```

양방향은 외래 키가 있는 쪽이 연관관계의 주인이다.  
다 쪽인 Member 테이블이 외래키를 가지고 있으므로, Member 가 연관관계의 주인이 된다.  

양방향 연관관계는 항상 서로를 참조해야 한다.  
연관관계 편의 메소드를 활용하는 편이 좋은데, 이 경우 무한루프에 빠지지 않도록 적절한 방어 로직을 추가한다.

### 일대다 

일대다 관계는 다대일 관계의 반대 방향이다.  
일대다 관계는 엔티티를 하나 이상 참조할 수 있으므로 자바 컬렉션인 Collection, List, Set, Map 중 하나를 사용해야 한다. 
일대다 맵핑은 존재하기는 하지만 거의 사용되지는 않는다.  
외래키가 다른 테이블에 존재하기 때문에 연관관계 처리를 위한 SQL 이 추가로 실행되어야 한다.  
이는 성능 문제와 관리가 부담스럽다.  일대다 맵핑 대신에 다대일 맵핑을 사용하도록 하자.


### 일대일 

일대일 관계는 양쪽이 서로 하나의 관계만 가진다.  
일대일 관계는 그 반대도 일대일 관계이고, 일대일 관계에서는 주 테이블이나 대상 테이블 중 어느 곳이나 외래 키를 가질 수 있다.  

- 주 테이블에 외래 키  
주 객체가 대상 객체를 참조하는 것처럼 주 테이블에 외래 키를 두고 대상을 참조한다.  
주 테이블이 외래 키를 가지고 있으므로 주 테이블만 확인해도 대상 테이블과 연관 관계가 있는 지 알 수 있다.

- 대상 테이블에 외래 키   
이 방법은 테이블 관계를 일대일에서 일대다로 변경할 때 테이블 구조를 그대로 유지할 수 있다.


#### 주 테이블에 외래 키  
```java
@Entity
public class Member {
    @Id @GeneratedValue 
    @Coluimn(name = "MEMBER_ID")
    private Long id;
    
    private String username;
    
    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;
}

// 단방향
@Entity 
public class Locker {
    @Id @GeneratedValue
    @Column(name = "LOCKER_ID")
    private Long id;
    
    private String name;
}

// 양방향 
@Entity 
public class Locker {
    @Id @GeneratedValue
    @Column(name = "LOCKER_ID")
    private Long id;
    
    private String name;
    
    @OneToOne(mappedBy = "locker")
    private Member member; 
}
```

Member 테이블에 LOCKER_ID 외래 키를 추가했다.  
일대일 관게이므로 @OneToOne 을 사용했다.  
양방향일 경우 연관관계의 주인을 정해야 한다.  
이 경우엔 Member 테이블에 외래 키가 존재하므로 Member 를 연관관계의 주인으로 설정한다.  

#### 대상 테이블에 외래 키 

- 단방향  
대상 테이블에 외래 키가 있는 단방향 관계는 JPA 에서 지원하지 않는다.  

- 양방향   
```java
@Entity
public class Member {
    @Id @GeneratedValue 
    @Coluimn(name = "MEMBER_ID")
    private Long id;
    
    private String username;
    
    @OneToOne(mappedBy = "member")
    private Locker locker;
}

@Entity 
public class Locker {
    @Id @GeneratedValue
    @Column(name = "LOCKER_ID")
    private Long id;
    
    private String name;
    
    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member; 
}
```


### 다대다 관계  
관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없다.  
그래서 보통 다대다관계를 일대다, 다대일 관계로 풀어내는 연결 테이블을 사용한다.  

하지만 객체는 테이블과 다르게 객체 2개로 다대다 관계를 만들 수 있다.  
연결 테이블에 단순히 연결된 테이블의 PK 만 존재한다면 별도 객체를 생성할 필요 없이 맵핑할 수 있다.

```java
@Entity
public class Member {
    @Id  
    @Coluimn(name = "MEMBER_ID")
    private String id;
    
    private String username;
    
    @ManyToMany 
    @JoinTable(name = "MEMBER_PRODUCT",
                joinColumns = @JoinColumn(name = "MEMBER_ID"),
                inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID"))
    private List<Product> products = new ArrayList<>();
}

@Entity 
public class Product {
    @Id @Column(name = "PRODUCT_ID")
    private String id;
    
    private String name;
}
```

회원 엔티티와 상품 엔티티를 @ManyToMany 를 이용하여 다대다 관계로 맵핑했다.  
중요한 점은 @ManyToMany + @JoinTable 을 사용하여 데이터베이스의 연결 테이블을 맵핑한 것이다.  
이렇게 하면 연결 테이블을 객체화 할 필요 없이 맵핑을 할 수 있다.  

- @JoinTable.name
    - 연결 테이블을 지정한다.
- @JoinTable.joinColumns 
    - 현재 방향인 회원과 맵핑할 조인 컬럼 정보를 지정한다.
- @JoinTable.inverseJoinColumns 
    - 반대 방향인 상품과 맵핑할 조인 컬럼 정보를 지정한다. 

```java
public void save() {
    Product product = new Product();
    product.setId("product");
    product.setName("상품");
    em.persist(product);
    
    Member member = new Member();
    member.setId("Member");
    member.setName("회원");
    member.getProducts().add(product); // 연관관계 설정 
    em.persist(member);
} 
```

다음과 같이 회원과 상품의 연결 관계를 다대다로 맵핑한 후 커밋되면 연결 테이블에도 저장된다. 

```SQL
insert into product ...
insert into member ...
insert into member_product ...
```

#### 다대다 관계 맵핑의 한계, 극복

@ManyToMany 를 사용할 경우 연결 테이블을 자동으로 처리해주므로 도메인 모델이 단순해진다.  
하지만 연결 테이블이 단순히 테이블의 PK 만 담고 끝나지 않는 경우가 훨씬 많다.  
연결 테이블에 부가적인 정보가 들어갈 경우, 더이상 @ManyToMany 를 사용할 수 없다.  
결국 연결 테이블을 맵핑하는 연결 엔티티를 만들고 1:N, N:1 관계로 만들어 주어야 한다.  


```java
@Entity 
public class Member {
    @Id @Column(name = "MEMBER_ID")
    private String id;
    
    @OneToMany(mappedBy = "member")
    // 연결 테이블인 MemberProduct 가 외래 키를 가지고 있으므로, 
    // 연결 테이블이 연관관계의 주인이 된다.
    private List<MemberProduct> memberProduct;
} 

@Entity 
public class Product {
    @Id @Column(name = "PRODUCT_ID")
    private String id;
    private String name;
}

// 연결 테이블 
@Entity 
@IdClass(MemberProductId.class)
public class MemberProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member; // MemberProductId.member 와 연결
    
    @Id
    @ManoToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product; // MemberProductId.product 와 연결 
    
    private int orderAmount;
}

// 식별자 클래스 
public class MemberProductId implements Serializable {
    private String member; // MemberProduct.member 와 연결
    private String product; // MemberProduct.product 와 연결 
    
    // hashcode and equals...
}
```


@IdClass 를 이용하여 복합 기본키를 맵핑했다. 
@IdClass 외에 @EmbeddedId 를 사용하는 방법도 있다.

복합키 위한 식별자 클래스는 다음과 같은 사항을 지켜야 한다.
- JPA에서 복합 키를 사용하려면 별도의 식별자 클래스를 만들어야 한다.
- Serializable 을 구현
- equals, hashcode 구현 
- 기본 생성자가 있어야 한다.
- 식별자 클래스는 public 이어야 한다. 
// 왜일까??

___
복합키를 사용하지 않고 간단히 다대다 관계를 구성하는 방법도 있다.  
복합키 대신 데이터베이스에서 자동으로 생성해주는 대리 키를 Long 값으로 사용하는 것이다.  
간편하고 비즈니스에 의존적이지 않고, ORM 시 복합 키를 만들지 않아도 되는 장점이 있다.

```java
@Entity 
public class Member {
    @Id @Column(name = "MEMBER_ID")
    private String id;
    
    @OneToMany(mappedBy = "member")
    // 연결 테이블인 MemberProduct 가 외래 키를 가지고 있으므로, 
    // 연결 테이블이 연관관계의 주인이 된다.
    private List<MemberProduct> memberProduct;
} 

@Entity 
public class Product {
    @Id @Column(name = "PRODUCT_ID")
    private String id;
    private String name;
}

// 연결 테이블 
@Entity 
public class Order {
    @Id @GeneratedValue 
    @Column(name = "ORDER_ID")
    private Long id;
    
    @Id
    @JoinColumn(name = "MEMBER_ID")
    private Member member; 
    
    @Id
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
    
    private int orderAmount;
}
```

다음과 같이 연결테이블의 PK 를 대리키를 사용함으로써,  
이전에 작업해야 했던 복잡한 복합키를 생성하는 절차가 필요 없어진다. 