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
