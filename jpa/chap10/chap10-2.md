### JPQL

- JPQL 은 객체지향 쿼리 언어다. 따라서 테이블을 대상으로 쿼리하는 것이 아니라 엔티티 객체를 대상으로 쿼리한다.
- JPQL 은 SQL 을 추상화해서 특정 데이터베이스에 의존하지 않는다.
- JPQL 은 결국 SQL 로 변환된다.

#### 기본 문법과 쿼리 API

JPQL 도 SQL 과 비슷하게 SELECT, UPDATE, DELETE 문을 사용할 수 있다.  
참고로 엔티티를 저장할 때는 EntityManager.persist() 메소드를 사용하면 되므로 INSERT 문은 없다.

##### SELECT 문

SELECT 문은 다음과 같이 사용한다.
```
SELECT m FROM Member AS m where m.username = 'Hello'
```

- 대소문자 구분
    - 엔티티와 속성은 대소문자를 구분한다. 반면 SELECT, FROM, AS 같은 JPQL 키워드는 대소문자를 구분하지 않는다.
- 엔티티 이름
    - JPQL 에서 사용한 Member 는 클래스 명이 아닌 엔티티명이다. 엔티티 명을 지정하지 않으면 클래스 명을 기본값으로 사용한다.
- 별칭은 필수
    - Member AS m 을 보면 Member 에 m 이란 별칭을 주었다. JPQL 은 별칭을 필수로 사용해야 한다. AS 는 생략 가능하다.
    
##### TypeQuery, Query

작성한 JPQL 을 실행하려면 쿼리 객체를 만들어야 한다. 쿼리 객체는 TypeQuery, Query 가 있는데 반환 타입을 명확하게 지정할 수 있으면 TypeQuery, 없으면 Query 객체를 사용한다.

```java
TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);
// 조회 대상이 Member 엔티티로 명확하므로 TypeQuery 를 사용
```

```java
Query query = em.createQuery("SELECT m.username, m.age from Member m");
// 조회 대상이 String, Integer 타입이므로 조회 대상 타입이 명확하지 않다.
// 이처럼 SELECT 절에서 여러 엔티티나 컬럼을 선택할 때는 반환 타입이 명확하지 않으므로 Query를 사용한다.
```


##### 결과 조회

다음 메소드들을 호출하면 실제 쿼리를 실행해서 데이터베이스를 조회한다.  

- query.getResultList() : 결과를 반환한다. 결과가 없으면 빈 컬렉션을 반환
- query.getSingleResult() : 결과가 정확히 하나일 때 사용한다. 결과가 없거나 1개보다 많으면 예외가 발생한다.

#### 파라미터 바인딩

JPQL 은 이름 기준 파라미터 바인딩을 지원한다.

```java
String usernameParam = "user1";

TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m where m.username = :username", Member.class);
// :username 이란 이름 기준 파라미터 정의

query.setParameter("username", usernameParam);
// username 파라미터 바인딩
List<Member> results = query.getResultList();
```

위치 기준 파라미터를 사용하려면 ? 다음에 위치 값을 주면 된다.

```java
List<Member> members = em.createQuery("SELECT m FROM Member m where m.username = ?1", Member.class)
                        .setParameter(1, usernameParam);
                        .getResultList();
```

---
```
JPQL 을 수정해서 파라미터 바인딩 방식을 사용하지 않고 직접 문자를 더해 만들 수도 있다.
"select m from Member m where m.username = '" + usernameParam + "'"
하지만 이 방법은 SQL 인젝션 공격에 취약하고, 성능 이슈도 있다.
파라미터 바인딩 방식을 사용하면 파라미터의 값이 달라도 JPA 는 같은 쿼리로 인식해서 JPQL 을 SQL로 파싱한 결과를 재사용할 수 있다.
```

#### 프로젝션

SELECT 절에 조회할 대상을 지정하는 것을 프로젝션 이라 한다.  
프로젝션 대상은 엔티티, 임베디드 타입, 스칼라 타입이 있다.

##### 엔티티 프로젝션

조회한 엔티티는 영속성 컨텍스트에서 관리된다.
```
SELECT m FROM Member m  
SELECT m.team FROM Member m 
``` 

##### 임베디드 타입 프로젝션

JPQL 에서 임베디드 타입은 엔티티와 거의 비슷하게 사용된다. 임베디드 타입은 조회의 시작점이 될 수 없다는 제약이 있다.  
다음은 임베디드 타입인 Address 를 조회의 시작점으로 사용한 잘못된 쿼리다.
```java
String query = "SELECT a FROM Address a";
```

시작점을 엔티티로 하고, 엔티티를 통해 임베디드 타입을 조회한다.
```java
String query = "SELECT o.address FROM Order o";
```

임베디드 타입은 엔티티 타입이 아닌 값 타입이다. 따라서 영속성 컨텍스트에서 관리되지 않는다. 

##### 스칼라 타입 

숫자, 문자, 날짜와 같은 기본 데이터 타입들을 스칼라 타입이라 한다.

##### 여러 값 조회
엔티티를 대상으로 조회하면 편리하겠지만, 꼭 필요한 데이터들만 선택해서 조회해야 할 때도 있다.  
프로젝션에 여러 값을 선택하면 TypeQuery 를 사용할 수 없고 대신 Query 를 사용하여 결과를 파싱해야 한다.

##### New 명령어
여러 대상을 프로젝션 할 때, 매번 Object 배열을 파싱하는 것은 불편하다.  
이런 객체 변환 작업을 편리하게 해줄수 있는 기능이 있다.

```java
public class UserDto {
    private String username;
    private int age;
    
    public UserDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```

```java
TypedQuery<UserDto> query = em.createQuery("SELECT new UserDTO(m.username, m.age) FROM Member m", UserDTO.class);
```

#### 페이징 API
JPA 는 페이징을 위한 추상 API를 제공한다.  

- setFirstResult(int startPosition) : 조회 시작 위치
- setMaxResults(int maxResult) : 조회할 데이터 수

```java
TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m ORDER BY m.username DESC");

query.setFirstResult(10);
query.setMaxResults(20);
query.getResultList();
```

위의 예제를 분석하면, FirstResult 의 시작이 10이므로 11번째부터 총 20건의 데이터를 조회한다.  
데이터베이스 마다 페이징 쿼리 문법은 다 다르지만, 데이터베이스 Dialect 덕분에 추상화해서 사용할 수 있다.

#### 집합과 정렬 

##### 집합
집합은 집합함수와 함께 통계 정보를 구할 때 사용한다.

|함수|설명|
|---|---|
|COUNT|결과 수를 구한다. 반환타입 : Long|
|MAX, MIN|최대, 최소 값을 구한다. 문자, 숫자, 날짜 등에 사용|
|AVG|평균 값을 구한다. 숫자 타입만 사용 가능. 반환타입 : Double|
|SUM|합을 구한다. 숫자 타입만 사용할 수 있다.|

**집합 함수 사용시 유의점**
- NULL 값은 무시되므로 통계에 잡히지 않는다.
- 값이 없는데 SUM,AVG,MAX,MIN 을 사용하면 NULL 값이 된다. COUNT는 0
- DISTINCT 를 집합 함수 안에 사용해서 중복된 값을 제거하고 나서 집합을 구할 수 있다.
- DISTINCT 를 COUNT에서 사용할 때 임베디드 타입은 지원하지 않는다.

##### GROUP BY, HAVING
GROUP BY 는 통계 데이터를 구할 때 특정 그룹끼리 묶어준다.
```sql
SELECT t.name, COUNT(m.age), SUM(m.age), AVG(m.age), MAX(m.age), MIN(m.age)
  FROM Member m LEFT JOIN m.team t 
GROUP BY t.name
```

HAVING 은 GROUP BY 와 함께 사용하는데 GROUP BY 로 그룹화한 통계 데이터를 기준으로 필터링 한다.
```sql
SELECT t.name, COUNT(m.age), SUM(m.age), AVG(m.age), MAX(m.age), MIN(m.age)
  FROM Member m LEFT JOIN m.team t 
GROUP BY t.name
HAVING AVG(m.age) > 10
```

통계 쿼리는 실시간성으로 사용하기엔 부담이 많다. 

##### 정렬
ORDER BY 는 결과를 정렬할 때 사용한다.

#### JPQL 조인
JPQL 도 조인을 지원하는데 SQL 조인과 기능은 같고 문법만 약간 다르다.

##### 내부 조인
내부조인은 INNER JOIN 을 사용한다. INNER 는 생략할 수 있다.

```java
String teamName = "teamA";
String query = "SELECT m FROM Member m INNER JOIN m.team t WHERE t.name = :teamName";

List<Member> members = em.createQuery(query, Member.class)
                        .setParameter("teamName", teamName)
                        .getResultList();
```

JPQL 조인의 특징은 연관 필드를 사용한다는 것이다.  
위 예제에서 Member 가 가지고 있는 필드인 team 을 이용해 조인을 했다.  
만약 다음과 같이 한다면 에러가 난다.
```
FROM Member m JOIN Team t
```

##### 외부 조인
JPQL 의 외부 조인은 다음과 같이 사용한다.
```SQL
SELECT m
FROM Member m LEFT [OUTER] JOIN m.team t
```
OUTER 는 생략 가능하다.

##### 컬렉션 조인 
일대다 관계나 다대다 관계처럼 컬렉션을 사용하는 곳이 조인하는 것을 컬렉션 조인이라 한다.

```SQL
SELECT t, m FROM Team t LEFT JOIN t.members m
```

##### 세타 조인 
WHERE 절을 사용해서 세타 조인을 할수 있다. 세타 조인은 내부 조인만 지원한다.  
세타 조인을 이용하면 관계 없는 엔티티도 조인할 수 있다.

```SQL
SELECT count(m) from Member m, Team t
WHERE m.username = t.name
```

#### 페치 조인 
페치 조인은 SQL 의 조인의 종류는 아니고 JPQL 에서 성능 최적화를 위해 제공하는 기능이다.  
연관된 엔티티나 컬렉션을 한 번에 같이 조회하는 기능인데 join fetch 명령어로 사용할 수 있다.

##### 엔티티 페치 조인 
페치 조인을 사용해서 회원 엔티티를 조회하면서 연관된 팀 엔티티도 함께 조회할 수 있다.  
```SQL
SELECT m FROM Member m join fetch m.team
```
join fetch 를 사용하면, 연관된 엔티티나 컬렉션을 함께 조회한다.  
위 에에서는 회원과 팀을 함께 조회하는데 일반적인 JPQL 조인과 다르게 team 에 별칭이 없다.  
페치 조인은 별칭을 사용할 수 없다.

실제로 실행되는 SQL 은 다음과 같다. 
```SQL
SELECT 
    M.*, T.*
FROM MEMBER M 
INNER JOIN TEAM T 
ON M.team_id = T.id
```

엔티티 페치 조인 JPQL 에서는 SELECT m 으로 회원만 조회했는데 실행된 SQL 은 회원과 팀을 함께 조회하고 있다.  
회원-팀을 지연 로딩으로 설정했다고 해도, 페치 조인은 회원과 팀을 함께 조회하므로 팀 엔티티는 프록시 객체가 아닌 실제 엔티티 이다.  
따라서 지연 로딩이 일어나지 않는다. 


##### 컬렉션 페치 조인 
일대다 관계인 컬렉션을 페치 조인해보자.  
```SQL
SELECT t 
FROM Team join fetch t.members
WHERE t.name = 'teamA'
```

팀을 조회하면서 페치 조인을 사용해 연관된 회원 컬렉션을 함께 조회한다.  
팀A => 멤버1,2 란 관계가 있을 때, 위의 쿼리를 실행하면 Team 이 주 엔티티이기 때문에
팀A 가 두개 조회된다. (왜지?)  


##### 페치 조인과 DISTINCT
JPQL 의 DISTINCT 는 SQL 에 DISTINCT 를 추가하는 것에 더해 애플리케이션 단에서 한 번 더 중복을 제거한다.  
직전의 예제는 팀A가 중복으로 조회된다. DISTINCT 를 추가하면
```SQL
SELECT DISTINCT t
FROM Team t JOIN FETCH t.members
WHERE t.name = 'teamA'
```

SQL 에 추가된 DELETE 는 행이 다르므로 SQL DISTINCT 는 효과가 없다

|row|팀|회원|
|---|---|---|
|1|teamA|member1|
|2|teamA|member2|

다음으로 애플리케이션에서 distinct 로 중복 데이터를 걸러낸다.  
select distinct t 의 의미는 팀 에니팉의 중복을 제거하는 것이다. 따라서 중복된 팀A는 하나만 조회되게 된다.  

##### 페치 조인과 일반 조인의 차이
JPQL 
```SQL
SELECT t
FROM Team t join t.members m
WHERE t.name = 'teamA'
```

SQL
```SQL
SELECT 
    T.*
FROM TEAM t
INNER JOIN MEMBER m ON t.id = m.team_id
WHERE t.name = 'teamA'
```

위 예제처럼 JPQL 에서 일반 조인을 사용했을 때에는 회원 컬렉션이 함께 조회가 되지 않는다.  
JPQL 은 결과를 반환할 때 연관관계를 고려하지 않고 단지 SELECT 절에 지정한 엔티티만 조회한다.  
따라서 팀 엔티티만 조회하고 연관된 회원 엔티티는 조회하지 않는다.

##### 페치 조인의 특징과 한계
페치 조인을 사용하면 SQL 한번으로 연관된 엔티티들을 함께 조회할 수 있어서 SQL 호출 횟수를 줄일 수 있다.  
엔티티에 적용하는 로딩 전략은 글로벌 로딩 전략이라 부르는데 페치 조인은 글로벌 로딩 전략보다 우선순위가 높다.  
예를 들어 엔티티에 지연 로딩을 설정해도 JPQL 에서 페치 조인을 사용하면 페치 조인이 적용되어 연관 엔티티를 함께 조회한다.  

페치 조인은 다음과 같은 한계가 있다.
- 페치 조인 대상에는 별칭을 줄 수 없다.
- 둘 이상의 컬렉션을 페치할 수 없다.
- 컬렉션을 페치 조인하면 페이징API를 사용할 수 없다.


#### 서브쿼리 
JPQL 의 서브쿼리는 WHERE, HAVING 절에서만 사용할 수 있고, SELECT, FROM 절에서는 사용할 수 없다.  