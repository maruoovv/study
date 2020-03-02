## 객체지향 쿼리 언어 

JPA는 복잡한 검색 조건을 사용해서 엔티티를 조회할 수 있는 다양한 쿼리 기술을 지원한다.

### 객체지향 쿼리 
EntityManager.find() 메소드를 사용하면 식별자로 엔티티 하나를 조회할 수 있고, 객체 그래프 탐색을 통해 연관된 엔티티 들을 찾을 수 있다.  
하지만 이 기능만으로는 복잡한 애플리케이션을 개발하기는 힘들다. 좀 더 현실적이고 복잡한 검색 방법이 필요하다.  
우리는 ORM 을 사용하므로 데이터베이스 테이블이 아닌 엔티티 객체를 대상으로 검색하는 방법이 필요하다.  
이를 위해 **JPQL** 이 만들어 졌다.  

- 테이블이 아닌 객체를 대상으로 검색하는 객체지향 쿼리다.
- SQL을 추상화하여 특정 데이터베이스에 의존하지 않는다.  

JPA 는 JPQL 뿐 아니라, Criteria Query, Native SQL 등도 지원한다.  
JPA 가 공식 지원하는 것은 아니지만 QueryDSL, jdbc, mybatis 등도 알아둘 필요가 있다.  

Criteria 나 QueryDSL 은 JPQL 을 편하게 작성하도록 도와주는 빌더 클래스 일 뿐이다. 따라서 JPQL 을 이해하는 것이 중요하다. 

#### JPQL

엔티티 객체를 조회하는 객체지향 쿼리다.  
JPQL 은 SQL 을 추상화하여 특정 데이터베이스에 의존하지 않는다. 데이터베이스 Dialect 만 수정하면 JPQL 을 수정하지 않아도 데이터베이스를 변경할 수 있다.  

회원 엔티티를 대상으로 JPQL 을 사용하는 간단한 예제를 보자.

```java
@Entity
public class Member {
    @Column(name = "name")
    private String username;
}
```

```java
String jpql = "select m from Member as m where m.username = 'sim'";
List<Member> results = em.createQuery(jpql, Member.class).getResultList();
```

예제는 회원이름이 sim 인 엔티티를 조회한다.  
Member 는 엔티티 이름, m.username 은 테이블 컬럼명이 아닌 엔티티 객체의 필드명이다.


#### Criteria 쿼리 

Criteria 는 JPQL 을 생성하는 빌더 클래스다. 
Criteria 의 장점은 문자가 아닌 query.select(m).where(...) 처럼 코드로 JPQL을 작성할 수 있다는 점이다.  
문자로 작성한 JPQL 보다 코드로 작성한 Criteria 의 장점은 다음과 같다.

- 컴파일 시점에 오류를 발견할 수 있다.
- IDE 자동완성의 지원을 받을 수 있다.
- 동적 쿼리를 작성하기 편하다.

이전의 JPQL 을 Criteria 쿼리로 변환해보자

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Member> query = cb.createQuery(Member.class);

// 루트 클래스 (조회를 시작할 클래스)
Root<Member> m = query.from(Member.class);

// 쿼리 생성
CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "sim"));
List<Member> results = em.createQuery(cq).getResultList();
```

Criteria 가 가진 장점은 많지만 모든 장점을 상쇄할 정도로 복잡하다.

#### QueryDSL

QueryDSL 도 Criteria 처럼 JPQL 빌더 역할을 한다.  
QueryDSL 은 JPA 표준은 아니고 오픈소스 프로젝트이다.

```java
JPAQuery query = new JPAQuery(em);
QMember member = QMember.member;

List<Member> members = query.from(member)
                            .where(member.username.eq("sim"))
                            .list(member);
```

QueryDSL 은 어노테이션 프로세서를 사용해서 쿼리 전용 클래스를 만들어야 한다.  
QMember 는 Member 엔티티 클래스를 기반으로 생성한 QueryDSL 전용 클래스이다.

#### 네이티브 SQL

JPA는 SQL을 직접 사용할 수 있는 기능을 지원하는데 이것을 네이티브 SQL 이라 한다.
JPQL 을 사용하더라도 특정 데이터베이스에 의존하는 기능을 사용해야 할 때가 있다. (Oracle CONNECT BY 등..)
네이티브 SQL 의 단점은 특정 데이터베이스에 의존하는 SQL 을 작성해야 한다는 것이다.

```java
String sql = "SELECT id, age, team_id, name FROM member WHERE name = 'sim'";
List<Member> members = em.createNativeQuery(sql, Member.class).getResultList();
```

#### JDBC, MyBatis..

JDBC나 MyBatis 를 JPA와 함께 사용하면 영속성 컨텍스트를 적절한 시점에 강제로 플러시해야 한다.  
jdbc든 sql 매퍼든 사용하면 모두 JPA를 우회하여 데이터베이스에 접근한다. JPA를 우회하는 SQL에 대해선 JPA가 전혀 인식을 하지 못한다.  
최악의 경우 영속성 컨텍스트와 데이터베이스를 불일치 상태로 만들어 데이터 무결성을 훼손할 수 있다.  
이런 이슈를 해결하는 방법은 JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트를 수동으로 플러시해서 데이터베이스와 영속성 컨텍스트를 동기화 하면 된다.