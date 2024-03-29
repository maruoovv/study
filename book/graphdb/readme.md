# Neo4j로 시작하는 그래프 데이터베이스 


### 그래프 데이터베이스란 무엇인가?
데이터베이스의 일종으로 모든 작업을 그래프와 그래프 이론을 이용하여 처리한다.  

#### 속성 그래프 모델 
그래프 데이터베이스의 근본적인 데이터 모델을 <b>속성 그래프 모델</b> 이라고 부른다.  
그래프 구조를 저장하기 위해 정점(노드)과 엣지(관계)를 사용한다.  
속성 그래프 모델을 살펴보면 다음과 같은 점을 알수 있다.  

> 고정된 스키마가 없다. 따라서 반구조화된 데이터를 처리하는데 적합하다.
> 
> 노드와 노드 속성은 관계형 데이터베이스의 테이블 레코드와 유사하다.  
> 
> 관계는 시작점과 끝점이 있는 방향을 갖고 있다.  
> 
> 관계는 명시적이고 속성을 가질수 있다.  

#### 그래프 데이터베이스를 사용하는 이유 

1. 복잡한 질의  
관계형 데이터베이스를 사용할때 많은 수의 테이블을 조인하여 데이터를 조회한다고 한다면, 쿼리의 복잡성 뿐 아니라 성능에도 영향을 끼친다.  
그래프 데이터베이스에서는 조인 작업을 수행할 필요 없이, 색인 없는 인접성 (index-free adjacency) <sup>(그래프 데이터베이스에서는 각 정점이 인접한 정점을 알고 있기 때문에 바로 찾아갈수 있다. 이를 색인 없는 인접성 이라고 한다.)</sup> 특성을 이용한 연결 관계를 통해 노드 간의 이동을 한다..  
노드 간의 관계는 이러한 조인 작업이 명시적으로 저장된 표현이라고 생각할 수 있다.  
   
이것이 그래프 데이터베이스의 핵심 성능 특성중 하나 인데, 시작 노드를 가져오면 시작 노드 부근과 연결된 노드들을 탐색하고,  
연결되지 않은 노드는 신경 쓰지 않는다. 대부분의 그래프는 모든 다른 노드와 연결돼 있는 것은 아니기 때문에 쿼리의 성능은 데이터 집합의 크기와 독립적이라고 볼 수 있다.  


2. 경로 탐색 질의  
그래프 데이터베이스에 적합한 유형의 질의는 서로 다른 데이터 요소가 어떻게 관련돼 있는지 확인하는 것이다.  
다시 말해, 그래프에서 서로 다른 노드 사이의 경로를 찾는 것이다.
   
#### 적합하지 않은 경우

1. 대규모 집합지향 쿼리
집합 연산, 집계 연산 등이 주된 연산이라면 그래프 데이터베이스 사용은 적절하지 않다.  
   
2. 글로벌 그래프 작업
그래프 이론은 그래프 전체를 분석하고 이해하는데 매력적이다. (노드 사이의 알려지지 않은 관계를 발견하는 등)  
하지만 이러한 작업은 리소스가 큰 작업이고 이러한 글로벌 그래프 작업은 주로 백그라운드에서 배치 처리 작업으로 사용되어 실시간 응답에는 적절하지 않다.  
   
3. 간단한 집계 중심 질의  
쓰기 / 읽기 패턴이 일정한 간단한 질의들은 일반적으로 그래프에서 비효율적으로 처리된다. 
   

Cypher : graph query language
