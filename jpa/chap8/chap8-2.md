### 즉시 로딩과 지연 로딩 

프록시 객체는 주로 연관된 엔티티를 지연 로딩할 때 사용한다.  
JPA는 개발자가 연관된 엔티티의 조회 시점을 선택할 수 있도록 두가지 방법을 제공한다.  

- 즉시 로딩 : 엔티티를 조회할 때 연관된 엔티티도 함께 조회한다.
    - em.find(Member.class, "member") 를 호출할 때 회원 엔티티와 연관된 팀 엔티티도 함께 호출한다.
    - @ManyToOne(fetch = FetchType.EAGER)
    
- 지연 로딩 : 연관된 엔티티를 실제 사용할 때 조회한다.
    - member.getTeam().getName() 처럼 조회한 팀 엔티티를 실제 사용하는 시점에 JPA가 팀 엔티티를 조회한다.
    - @ManyToOne(fetch = FetchType.LAZY)
    
#### 즉시 로딩
즉시 로딩을 사용하려면 fetch 속성을 FetchType.EAGER 로 지정한다. 

```java
@Entity 
public class Member {
    // ...
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    // ...
}
```

회원 안의 팀을 즉시 로딩으로 설정했다. em.find() 를 하는 순간 팀도 함께 조회한다.  
JPA구현체는 즉시 로딩을 최적화하기 위해 가능하면 조인 쿼리를 사용한다.

```SQL
SELECT
    M.member_id AS member_id,
    M.team_id AS team_id,
    M.username AS username,
    T.team_id AS team_id,
    T.name AS name
FROM
    MEMBER M LEFT OUTER JOIN TEAM T
        ON M.team_id = T.team_id
WHERE
    M.member_id = 'member'
```

위의 쿼리를 보면 outer join 을 사용하고 있다.  
현재 회원 테이블의 team_id 외래 키는 null을 허용하고 있기 때문에 팀에 소속되지 않은 회원이 존재할 수 있다. 따라서 outer join 이 사용됐다.  
외래키에 not null 제약조건을 사용하면 outer join 대신 inner join 을 사용할 수 있다.  
JPA에게도 이 사실을 nullable = false 옵션을 주어 알려주어야 한다.  

#### 지연 로딩 
지연 로딩을 사용하려면 FetchType.LAZY 로 지정한다. 

```java
@Entity 
public class Member {
    //...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    //...
}
```  

```java
Member member = em.find(Member.class, "member");
Teamteam = member.getTeam(); // 객체 그래프 탐색
team.getName(); // 팀 객체 실제 사용
```

회원과 팀을 지연 로딩으로 설정했다.  
따라서 em.find() 시에 회원만 조회하고 팀은 조회하지 않는다. 대신 회원의 team 멤버변수에 프록시 객체를 넣어둔다.  
이 프록시 객체는 실제 사용될 때까지 데이터 로딩을 미룬다.  

```
조회 대상이 영속성 컨텍스트에 이미 있을 경우엔 프록시 객체가 아닌 실제 객체를 사용한다.
```

---
처음부터 모든 엔티티를 올려두는 것은 현실적이지 않고, 필요할 때마다 SQL을 실행하여 연관된 엔티티를 지연 로딩하는 것도 최적화 관점에서는 좋은 것만은 아니다.  
비즈니스 로직에 따라 상황에 맞추어 개발하자.