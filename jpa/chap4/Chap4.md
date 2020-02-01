## 엔티티 매핑 

JPA를 사용하는데 중요한 엔티티와 테이블을 맵핑하는 방법을 알아보자.

#### @Entity

테이블과 클래스를 맵핑해주는 어노테이션

|속성   | 기능  | 기본값  |
|---|---|---|
|name   | JPA에서 사용할 엔티티 이름을 지정. 보통 클래스 이름을 사용한다.|  클래스 이름 |

@Entity 적용 시 주의사항 
- 기본 생성자는 필수다.
- final 클래스, enum, interface, inner 클래스 사용 불가 
- 저장할 필드에 final 사용 x

#### @Table

엔티티와 맵핑 할 테이블을 지정한다.

|속성|기능|기본값|
|---|---|---|
|name|맵핑할 테이블 이름|엔티티 이름|
|catalog|catalog 기능이 있는 데이터베이스에서 catalog 맵핑||
|schema|schema 기능이 있는 데이터베이스에서 schema 맵핑||
|uniqueConstraints|DDL 생성 시 유니크 제약조건을 만든다. 스키마 자동 생성 기능을 사용해서 DDL 을 만들 때만 사용된다.||


#### 데이터베이스 스키마 자동 생성 

JPA 는 데이터베이스 스키마를 자동으로 생성해주는 기능을 지원한다.
다음 속성을 지정하면 스키마 자동 생성을 지원해준다.   
```xml
<property name="hibernate.hbm2ddl.auto" value="create" />
```

애플리케이션 실행 시점에 데이터베이스 테이블을 자동으로 생성한다.  
개발 환경에서만 사용하거나 개발 단계에 참고하는 정도로만 사용하는게 좋다.  

#### 기본 키 맵핑 

JPA가 제공하는 데이터베이스 기본 키 생성 전략은 다음과 같다.

- 직접 할당 : 기본키를 애플리케이션에서 직접 할당 
- 자동 생성 : 대리 키 사용방식
    - IDENTITY : 기본 키 생성을 데이터베이스에 위임 
    - SEQUENCE : 데이터베이스 스퀀스를 사용해서 기본 키를 할당 
    - TABLE : 키 생성 테이블 사용 

자동 생성 전략이 다양한 이유는 벤더마다 지원하는 방식이 다르기 때문이다.  
(ex : 오라클은 시퀀스 제공, mysql 미제공)  

- 기본 키 할당 

```java
@Entity
class User {
    //@Id 어노테이션으로 맵핑 한다.
    @Id
    @Column
    private String id;
}
```
기본 키 할당 전략은 em.persist 로 엔티티를 저장하기 전에 어플리케이션에서 기본 키를 직접 할당한다.  
기본 키를 지정하지 않을 경우 예외가 발생한다.  

- IDENTITY 전략 
기본 키 생성을 데이터베이스에 위임한다.  
@GeneratedValue 어노테이션을 사용하고, 식별자 생성 전략을 지정한다.  
IDENTITY 전략은 strategy 속성 값을 GeneratedType.IDENTITY 로 지정한다.
이 전략은 기본 키 값을 얻어오기 위해 엔티티 저장 전에 데이터베이스를 추가로 조회한다. 
```java
@Entity
class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```
---
엔티티가 영속 상태가 되려면 식별자가 반드시 필요하다.  
이 전략은 엔티티를 데이터베이스에 저장해야 식별자를 구할 수 있으므로 em.persist 시 즉시 SQL이 데이터베이스에 전달된다.  
따라서 이 전략은 쓰기 지연이 발생하지 않는다.
---

- SEQUENCE 전략 
데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 데이터베이스 오브젝트이다.  
오라클, postgre, db2, h2 등에서 사용이 가능하다.  
데이터베이스에 시퀀스를 생성하고, 맵핑해준다.  

```java
@Entity
@SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "BOARD_SEQ",
        initialvalue = 1, allocationSize = 1
)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "BOARD_SEQ_GENERATOR")
    private Long id;
}
```
시퀀스 전략은 em.persist 를 호출할 때 먼저 데이터베이스 시퀀스를 사용해서 식별자를 가져오고, 엔티티에 할당한 후 
엔티티를 영속성 컨텍스트에 저장한다.  
이후 커밋시에 플러시가 일어날 때 엔티티를 데이터베이스에 저장한다.

- TABLE 전략
키 생성 전용 테이블을 하나 만들어 데이터베이스 시퀀스처럼 사용하는 전략이다.
```java  
@Entity
@TableGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "MY_SEQUENCES",
        pkColumnValue = "BOARD_SEQ", allocationSize = 1
)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,
                    generator = "BOARD_SEQ_GENERATOR")
    private Long id;
}
```

#### 필드와 컬럼 맵핑
 
##### @Column
객체 필드를 테이블 컬럼에 맵핑한다.  

##### @Enumerated
자바의 enum 타입을 테이블 컬럼에 맵핑한다.

- EnumType.ORDINAL : enum 의 순서를 데이터베이스에 저장 (기본값) 
- EnumType.STRING : enum 의 이름을 데이터베이스에 저장.

EnumType.ORDINAL 은 데이터베이스에 저장되는 크기는 작지만, 
저장하고 난 후 enum 의 순서가 변경되면 데이터베이스에 저장된 값은 바뀌지 않으므로 위험을 초래한다.  

##### @Temporal
날짜 타입을 맵핑한다.

##### @Lob
데이터베이스 BLOB, CLOB 타입과 맵핑한다.  

##### @Transient 
이 필드는 맵핑하지 않는다. 데이터베이스에 저장하지도 않고 조회하지도 않는다.  
  
