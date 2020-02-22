## 연관관계 맵핑 기초

객체는 참조를 사용해서 관계를 맺고 테이블은 외래 키를 사용해서 관계를 맺는다.  
ORM 에서 가장 어려운 부분이 객체 연관관계와 테이블 연관관계를 맵핑하는 일이다.  

### 단방향 연관관계

연관관계 중에서 다대일(N:1) 단방향 관계를 먼저 이해해야 한다.  
회원과 팀의 관계를 통해 연관관계를 알아보자.

```
- 회원과 팀이 있다.
- 회원은 하나의 팀에만 소속될 수 있다.
- 회원과 팀은 다대일 관계다.
``` 

참조를 통한 연관관계 (객체) 는 언제나 단방향이다. 객체간에 연관관계를 양방향으로 하려 한다면, 반대쪽에도 참조를 저장해야한다.  
정확히 이야기 하면 양방향 관계가 아닌, 서로 다른 단방향 관계 2개가 생기는 것이다.
반면에 테이블 연관관계는 외래 키 하나로 양방향으로 조인이 가능하다.

```java
// 단방향 연관관계
class Member {
	Team team;
}
class Team {
	
}
```

```java
//양방향 연관관계 (단방향 관계 2개)
class Member {
	Team team;
}

class Team {
	List<Member> members;
}
```

#### 객체 연관관계 vs 테이블 연관관계 정의
- 객체는 참조로 연관관계를 맺는다.
- 테이블은 외래 키로 연관관계를 맺는다.

##### 객체 관계 매핑
```java
@Entity
public class Member {
	@Id
	@Column(name = "MEMBER_ID")
	private String id;
	private String name;
	
	// 연관관계 맵핑
	@ManyToOne
	@JoinColumn(name = "TEAM_ID")
	private Team team; // 팀의 참조
	
	public void setTeam(Team team) {
		this.team = team;
	} 
}

@Entity
public class Team {
	@Id 
	@Column(name = "TEAM_ID")
	private String id;
	private String name;
}
```

- @ManyToOne
	- 다대일 관계를 나타내는 맵핑.
- @JoinColumn
	- 외래키를 맵핑할 때 사용
	- name 속성에 맵핑할 외래 키 지정.
	- 이 어노테이션을 생략하면 외래 키를 찾을 때 기본 전략 사용
	- 필드명 + _ + 참조하는 테이블으 컬럼명


### 연관관계 사용

연관관계를 등록, 수정, 삭제, 조회 하는 예제를 통해 연관관계 사용을 알아보자.

#### 저장
```java
public void save() {
	Team team1 = new Team("team1", "팀1");
	em.persist(team1);
	
	Member member1 = new Member("member1", "회원1");
	member1.setTeam(team1);
	em.persist(member1);
	
	Member member2 = new Member("member2", "회원2");
	member2.setTeam(team1);
	em.persist(member2);
}
```

**주의** : JPA에서 엔티티를 저장할 때 연관돈 모든 엔티는 영속 상태여야 한다.

#### 조회

연관관계가 있는 엔팉를 조회하는 방법은 크게 2가지다.
- 객체 그래프 탐색
- JPQL 사용

```java
// 객체 그래프 탐색 

Member member = em.find(Member.calss, "member1");
Team team = member.getTeam();
```

```java
// JPQL 사용
String jpql = "select m from Member m join m.team t where t.name=:teamName";
List<Member> result = em.createQuery(jpql, Member.class)
			.setParameter("teamName", "팀1")
			.getResultList();

// member1, member2
```

#### 수정
```java
public void update() {
	Team team2 = new Team("team2", "팀2");
	em.persist(team2);
	
	Member member = em.find(Member.class, "member1");
	member.setTeam(team2);
}
```

엔티티의 변경 내용과 마찬가지로, 연관 관계 수정 내용은 트랜잭션을 커밋할 때 플러시가 일어나면서 데이터베이스에 반영된다.

#### 제거 
```java
public void delete() {
	Member member1 = em.find(Member.class, "member1");
	member1.setTeam(null);
}
```

### 양방향 연관관계
양방향 연관관계를 알아보자.  
양방향 연관관계는 단방향 연관관계를 양쪽에 2개를 만드는 것과 같다.

```java
@Entity
public class Member {
	@Id
	@Column(name = "MEMBER_ID")
	private String id;
	private String name;
	
	// 연관관계 맵핑
	@ManyToOne
	@JoinColumn(name = "TEAM_ID")
	private Team team; // 팀의 참조
	
	public void setTeam(Team team) {
		this.team = team;
	} 
}

@Entity
public class Team {
	@Id 
	@Column(name = "TEAM_ID")
	private String id;
	private String name;
	
	@OneToMany
	@JoinColumn(mappedBy = "team")
	private List<Member> members = new ArrayList<Member>();
}
```

팀과 회원은 일대다 관계이므로, 팀에는 회원의 참조를 OneToMany 로 설정했다.  
mappedBy 속성은 양방향 맵핑일 때, 반대쪽 맵핑의 필드 이름을 값으로 준다.

### 연관관계의 주인
객체간의 관계에선 엄밀히 말해 양방향 연관관계 라는 것이 없다.  
서로 다른 단방향 연관관계 2개를 양방향인 것처럼 보이게 했을 뿐이다.  
테이블은 외래 키 하나로 양방향 연관관계 관리가 가능하지만,  
객체는 단방향 참조가 2개 발생하게 되는데, 둘 중 어떤 관계를 사용하여 외래 키 관리를 해야할까?  
이러한 객체와 테이블 간의 차이로 인해 JPA는 두 연관관계 중 하나를 정하여 외래키를 관리해야 하는데 이것을 **연관관계의 주인** 이라 한다.

연관관계의 주인만이 데이터베이스 연관관계와 맵핑되고, 외래키를 관리 할 수 있다.  
주인이 아닌 쪽은 읽기만 할 수 있다.

- 연관관계의 주인이 아닌 쪽이 mappedBy 속성을 사용해 속성의 값으로 주인을 지정한다.
- 연관관계의 주인은 테이블에 외래 키가 있는 곳으로 해야 한다.


### 양방향 연관관계 저장 
```JAVA
public void save() {
    // 팀1 저장
    Team team1 = new Team("team1", "팀1");
    em.persist(team1);
    
    // 회원1 저장
    Member member1 = new Member("member1", "회원1");
    member1.setTeam(team1); // 연관관계 설정 member1 -> team1
    em.persist(member1);
    
    // 회원2 저장
    Member member2 = new Member("member2", "회원2");
    member2.setTeam(team1); // 연관관계 설정 member2 -> team1
    em.persist(member2);
}
```
양방향 연관관계는 연관관계의 주인이 외래키를 관리한다.   
따라서 주인이 아닌 쪽의 방향에 다음과 같이 설정하지 않아도 데이터베이스에 반영된다.

```java
team1.getMembers().add(member1); // 연관관계의 주인이 아니므로 무시
``` 
연관관계의 주인이 아닌 쪽에 이런식으로만 설정하면, 데이터베이스에 정상 반영되지 않는다.

### 양방향 연관관계 주의점 
하지만 객체의 입장에서 보면 양쪽 방향에 모두 값을 입력해주는 것이 안전하다.

```JAVA
public void save() {
    // 팀1 저장
    Team team1 = new Team("team1", "팀1");
    em.persist(team1);
    
    // 회원1 저장
    Member member1 = new Member("member1", "회원1");
    member1.setTeam(team1); // 연관관계 설정 member1 -> team1
    team1.getMembers().add(member1); // 연관관계 설정 team1 -> member1
    em.persist(member1);
    
    // 회원2 저장
    Member member2 = new Member("member2", "회원2");
    member2.setTeam(team1); // 연관관계 설정 member2 -> team1
    team1.getMembers().add(member2); // 연관관계 설정 team1 -> member2
    em.persist(member2);
}
```
___
#### 주의!  
**연관관계의 주인은 비즈니스 로직 상 중요도에 따라 정하는 것이 아닌,
단순하게 테이블의 외래 키의 위치에 따라 결정해야 한다.**