# DBMS와 버퍼

DBMS의 버퍼 매니저에 대해 더 알아보자.  
버퍼는 데이터베이스 성능에 굉장히 중요한 영향을 미친다.  

## DBMS와 기억장치의 관게

DBMS가 사용하는 대표적인 기억장치는 하드디스크, 메모리 이다.

- 하드디스크 (HDD)  
현재 많은 DBMS 는 HDD에 데이터를 저장한다.  
하드디스크는 기억장치 계층의 2단계로, 접근 속도와 비용 면에서 평균적인 수치를 가지고 있다.  

- 메모리  
메모리는 디스크에 비해 기억 비용이 비싸다.  
따라서 하드웨어 1대에 탑재할 수 있는 양이 많지 않고, 규모 있는 시스템의 경우엔 데이터베이스 내부 데이터를 모두 메모리에 올리는 것을 불가능 하다.

- 버퍼를 활용한 속도 향상  
DBMS는 데이터의 일부라도 메모리에 올려 성능 향상을 도모한다.  
자주 접근하는 데이터를 메모리에 올려둔다면, SQL 구문이 실행될 때 디스크에서 데이터를 가져올 필요 없이 메모리에서 빠르게 읽을 수 있다.  
일반적인 SQL 구문의 실행시간 대부분이 디스크 I/O 에 집중되기 때문에, 디스크 접근을 줄인다면 큰 성능 향상이 가능하다.  

이렇게 성능 향상을 목적으로 데이터를 저장하는 메모리를 **버퍼** 또는 **캐시** 라고 부른다.  
버퍼에 데이터를 저장하는 정책을 관리하는 것이 DBMS의 버퍼 매니저이다.   

## 두종류의 버퍼 

DBMS가 데이터를 유지하기 위해 사용하는 메모리는 크게 두 종류이다.

- 데이터 캐시  
데이터 캐시는 디스크에 있는 데이터의 일부를 메모리에 유지하기 위해 사용하는 메모리 영역이다.  

- 로그 버퍼  
로그 버퍼는 갱신 처리 (INSERT, DELETE, UPDATE, MERGE) 와 관련이 있다.  
DBMS는 갱신과 관련된 SQL 을 입력 받으면, 바로 저장소의 데이터를 변경하는 것이 아니라, 로그 버퍼에 변경 정보를 보내고 이후에 디스크에 변경을 수행한다.
  
SQL구문을 실행할 때 디스크의 데이터를 바로 변경하는 것이 가장 간단한 방법이다.  
로그 버퍼를 이용해 시점의 차이를 두는 이유는 성능을 높이기 위해서 이다.  
데이터를 갱신할 때에도 상당한 시간이 소요 되는데, 갱신을 바로 바로 적용 한다면 그만큼 디스크 I/O는 증가할 것이고 사용자는 장시간 대기하게 될 수 있다.  

대부분의 DBMS는 두가지 역할을 하는 메모리 영역을 가지고 있고, 용도에 따라 크기 변경이 가능하다.
DBMS는 저장소의 느림을 어떻게 보완할 것인가를 계속해서 고민해온 미들웨어이다.

## 메모리의 성질이 초래하는 트레이드오프

- 휘발성  
메모리에는 데이터의 영속성이 없다. 하드웨어가 꺼지면 메모리의 모든 데이터가 사라지는데, 이러한 성질을 **휘발성** 이라 한다.  
DBMS를 껐다 켜면 버퍼의 모든 데이터가 사라진다. 따라서 장애가 발생했을 시 메모리에 모든 데이터가 날아갈 가능성이 있기 때문에  
메모리에 영속성이 없는 이상 디스크를 완전히 대체하는 것은 불가능 하다.  

- 휘발성의 문제점  
휘발성의 가장 큰 문제점은 메모리에 있던 데이터가 모두 사라진다면 데이터 부정합이 발생하는 것이다.  
데이터 캐시의 데이터는 장애로 인해 데이터가 다 사라지더라도, 원본 데이터가 디스크에 있기 때문에 데이터를 한번 더 읽어들이면 괜찮다.  

문제는 로그 버퍼에 존재하던 데이터가 디스크 데이터에 반영되기 전에 장애가 발생한다면 데이터는 완전히 사라져 복구가 불가능 하다.  
이는 사용자가 수행한 갱신 정보가 사라진다는 의미이다.  

로그 버퍼에 전달된 갱신 정보가 장애 시 사라지는 현상은 DBMS가 비동기 갱신을 하는 한 언제든 발생할 수 있는 문제이다.  
이를 해결하기 위해 DBMS는 커밋 시점에 반드시 갱신 정보를 로그 파일에(영속적인 저장소) 씀으로써, 정합성을 유지할 수 있게 한다.  
커밋 때는 반드시 디스크 I/O 에 동기식 접근이 일어 나므로, 지연이 발생할 수 있다.  
디스크에 동기 처리를 한다면 데이터 정합성은 높아지지만 성능은 낮아진다.

## 시스템 특성이 따른 트레이드오프

- 데이터캐시와 로그버퍼의 크기  
대부분의 DBMS의 기본 설정은 데이터 캐시에 비해 로그 버퍼의 초기 값은 굉장히 작다.  
이는 데이터베이스가 기본적으로 검색을 메인으로 처리한다고 가정하기 떄문이다.  

검색 처리를 할 때는 대상 레코드가 수백~수천만 건이 될수 있지만,  
갱신 처리는 보통의 경우엔 한 트랜잭션에서 한건~수만건 정도이다.  
따라서 갱신 처리에 비싼 메모리를 많이 사용하는 것 보다 자주 검색되는 데이터를 캐시에 올려놓는 것이 좋다고 생각하기 때문이다.  

> mysql 매뉴얼에 보면, 서버가 데이터베이스 전용이라면 물리 메모리의 80% 를 버퍼 풀로 할당해도 괜찮다. 라고 되어 있다.  
> A larger buffer pool requires less disk I/O to access the same table data more than once.  
> On a dedicated database server, you might set the buffer pool size to 80% of the machine's physical memory size.  
> Be aware of the following potential issues when configuring buffer pool size, and be prepared to scale back the size of the buffer pool if necessary.
> https://dev.mysql.com/doc/refman/5.7/en/innodb-parameters.html

물론 이는 시스템의 성격에 따라 다를 수 있다.  
어떤 시스템이 검색보다 갱신이 많다면, 로그 버퍼의 크기를 늘려주는 튜닝이 필요할 수 있다.  

- 검색과 갱신 중 중요한 것  
검색과 갱신 중 어떤 것이 우선되어야 하는가라는 트레이드오프에 직면한다.  
메모리는 비싼 자원이므로 모든 것을 커버할 수 없으므로 어떤 것을 우선시 할 것인지 판단해야 한다.  
이를 위해선 시스템의 성격과 분석이 필요하다.  
아무 생각 없이 기본 설정을 이용한다면, 시스템의 성능 저하를 초래할 수 있다.  


## 추가적인 메모리 영억 '워킹 메모리'
DBMS 는 앞의 버퍼 2개 이외에도 일반적으로 '워킹 메모리' 라고 불리는 메모리 영역을 하나 가지고 있다.
이 메모리 영역의 이름과 관리 방법은 DBMS에 따라 다르다.  
워킹 메모리는 정렬 또는 해시 관련 처리에 사용되는 작업용 영역이다.    
정렬은 ORDER BY, 집합 연산, 윈도우 함수 등의 기능을 사용할 때 실행되고, 해시는 테이블 등의 결합에서 해시 결합이 사용되는데 실행된다.  
  
이 영역이 성능적으로 중요한 이유는, 데이터의 양보다 이 영역이 작은 경우가 생기면 저장소를 사용하기 떄문이다.  
이는 OS 동작에서 말하는 스왑(swap) 과 같은 것이다.  

많은 dbms 는 워킹 메모리가 부족할 때 사용하는 임시적인 영역을 갖고 있다.  
1) Oracle : TEMP tablespace
2) Sql Server : TEMPDB
3) PostgreSQL : pgsql_tmp  
이러한 영역들은 저장소 위에 있으므로 접근 속도가 느리다.  

- 부족하다면?  
메모리가 부족하다고 처리가 멈추거나 에러가 발생하는 것은 아니다.  
하지만 메모리에서 작동하고 있을 때는 빠르게 작동하다가, 메모리가 부족해지는 순간 갑자기 느려지는 순간적인 변화가 문제이다.  
또한 이 영역은 여러 SQL 구문 들이 공유 하므로, 여러개의 SQL 구문을 실행하면 메모리가 넘치는 경우가 있다.  

자바가 구동되는 jvm 같은 경우는 힙 크기가 부족하면 oom 오류를 발생시켜 모든 처리를 중단시켜 버린다.  
하지만 데이터베이스는 이러한 선택을 하지 않고, 비록 느려지더라도 처리를 하도록 만든 시스템이다.  

