JPA 는 영속성 컨텍스트를 사용하여 엔티티들을 관리한다.  
이 영속성 컨텍스트는 캐시 역할을 하기도 한다.  
영속성 컨텍스트의 관리 하에 있는 엔티티들은 굳이 db 조회 없이 캐시에서 꺼내볼수 있다는 장점이 있다.  
하지만 엔티티가 변경된다면 실제 db 에도 반영을 해주어야 할텐데 , 
JPA 는 언제 어떤 방식으로 변경을 감지 할까?

일단 '시점' 은 명백히 알려져 있다.

- Transactional annotation 을 사용하여 메소드 종료 후에 트랜잭션이 커밋 될때
- 직접 entityManager 를 사용하여 트랜잭션을 커밋할때
- jpa 내부 구현에 Transcational 이 선언된 메소드를 호출하는 시점에

그럼 데이터가 변경됐음을 어떻게 감지 할까?  
dirty check 라는 메커니즘을 사용한다.  
dirty check 는 여기서만 사용되는 개념은 아니고, cs 전반적으로 광범위하게 사용된다.  
jpa 의 구현체인 hibernate 는 영속성 컨텍스트가 관리하는 엔티티들의 정보를 갖고 있고,  
트랜잭션이 커밋되는 시점의 해당 엔티티의 필드들의 이전/현재 값을 비교하여 dirty 여부를 체크한다.  
실제 구현은 더 복잡하긴 하지만.. 실제 값 비교하고 dirty 여부를 판단하는 코드를 발췌 했다.

[https://github.com/hibernate/hibernate-orm/blob/main/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java#L302](https://github.com/hibernate/hibernate-orm/blob/main/hibernate-core/src/main/java/org/hibernate/type/TypeHelper.java#L302)

만약 어떤 메소드가 수정은 없지만 여러 repository 를 조회해서 Transcational 을 사용해야 한다면,
이 메소드는 종료시점에 불필요한 dirty check 를 하게 되게 될것이다.  
이런 경우를 방지하기 위해 Transcational(readOnly = true) 옵션을 준다면, 종료시에 dirty check 를 하지 않을수 있다.