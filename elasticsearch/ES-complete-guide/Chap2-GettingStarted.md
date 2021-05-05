## Getting Started

- ES Directory
    - config/elasticsearch.yaml
        - 엘라스틱 서치 관련 설정
    - config/jvm.options
        - jvm 관련 설정
    - lib
        - es 가 필요한 라이브러리 파일들
    - modules
        - built-in modules
     
### Understanding the basic architecture

- Node
    - ES 를 시작한건 노드를 시작한것 
    - 노드는 데이터를 저장하는 ES의 인스턴스
    - 각 노드는 데이터를 분산해서 저장 
    - 노드는 머신을 뜻하는게 아니라 인스턴스. 한 머신 안에 여러개의 노드를 띄우는 것도 가능 

- 클러스터
    - 각 노드는 클러스터에 속해있다.
    - 클러스터는 전체 데이터를 갖고 있다.
    - 각 클러스터는 독립적이다.
    - 일반적으로 멀티 클러스터를 구성하더라도 각 클러스터는 다른 목적으로 사용한다.
    - 모든 엘라스틱서치의 노드는 어떤 클러스터에 속해있다. (노드가 단 하나라도)

#### 클러스터는 어떻게 구성될까?

노드가 시작될때, 이미 구성된 클러스터에 조인 하거나, 자신의 클러스터를 만든다.

#### ES 는 어떻게 데이터를 노드들에 분산 저장하고, 어느 데이터가 어느 노드에 있는지 알까?

각각의 데이터는 클러스터에 document(JSON Object) 란 형태로 ES 의 메타데이터와 함께 저장된다.  
ES 에 보낸 데이터는 _source 필드로 저장된다.  
ES 의 모든 document 는 index 로 그룹핑 된다.  

### Inspecting the cluster

- cluster health check
    - GET /_cluster/health
    - /_cluster : API
    - /health : command
- API : underscore 로 시작 (컨벤션)

### Sharding and scalability

- ES 클러스터에 노드를 추가 하는걸 Sharding 이라 한다.  

- Sharding
    - index 를 작은 조각들로 나누는것 
    - 각 조각은 shard 라 부름
    - sharding 은 노드나 클러스터 레벨이 아닌 인덱스 레벨에서 수행됨. 
    - 데이터를 수평 분할하기 위해 사용 
    - 샤드는 어느 한 노드에 속하는데, 꼭 각 샤드가 별도의 노드에 속할 필요는 없다.
    - 각 샤드는 Apache Lucene index 다.
    
- Sharding 을 하는 이유
    - 더 많은 document 저장을 위해
        - 단일 노드의 capacity 가 한정적이라면, 노드보다 큰 데이터를 저장할수 없음. 샤드를 이용하면 분할해서 저장 가능
    - 성능향상
        - 쿼리실행시, 여러개의 샤드에 병렬적으로 질의 가능 


### Understanding replication

노드가 있는 서버가 죽는다면 어떻게 할것인가? 하드웨어는 언제든 죽을수 있다..  
ES 는 기본적으로 replication 을 제공해준다. (별도 설정 없이) 

- Replication 은 인덱스 레벨에 설정된다. 
- 샤드의 복사본을 만들고, 이를 replica shards 라 부른다.  
- 복제 대상이 된 샤드는 primary shard 라 부른다
- primary + replica 를 합쳐서 replication group 이라 부른다.
- replica shards 는 샤드의 완전한 복제본이고, primary 샤드와 마찬가지로 검색 요청을 처리할수 있다. 
- 인덱스 생성 시 replica 수를 정할수 있다.  
- replica shards 는 그것의 primary shard 와 **다른 노드**에 위치한다.
- 가용성과 성능을 향상 시키기 위해 사용된다.

```
단일 샤드의 primary + replica2 개라고 하더라도, 검색 쿼리가 들어 왔을때 세개의 샤드에 병렬적으로 요청이 가능하다.
따라서 처리량을 늘릴수 있다.
```

replication 외에 ES는 백업을 위해 Snapshot 기능을 제공한다. 

### 인덱스 생성하기

```
PUT /pages
```

pages 란 인덱스를 생성하고 클러스터의 상태를 보면 status 가 yellow 로 나온다.  
```
GET /_cluster/health  
```
```
GET /_cat/indices?v
```
방금 생성한 pages 인덱스의 status 가 yellow 다.  
pages 는 primary 1개 + replica 1개로 생성되었는데, replica 가 노드에 할당되지 않아서이다.  
replica 는 primary 와 같은 노드에 할당될수 없는데,  
생성한 클러스터는 single node 로 생성했으므로 ES 가 replica shard 를 할당하지 못한것이다.  
이를 해결하려면 노드를 추가해주어야 한다.  

### 노드의 역할

- Master-eligible 노드
    - 클러스터는 하나의 마스터 노드가 존재하고, 마스터 노드가 다운되거나 끊어진 경우 다른 후보 노드들 중에서 마스터가 선출됨
    - 클러스터의 상태를 관리
    - 투표에 의해 선출되는 노드 
    - create/delete indices, node tracking
    
- 데이터 노드
    - 데이터를 저장
    - 검색 쿼리, data crud, 집계등의 데이터 관련 작업 수행
    - 데이터를 직접적으로 다루기 때문에 리소스가 많이 필요.
    
- Ingest 노드
    - 데이터를 인덱싱 하기 전에 다양한 전처리를 할수 있다. ex) field 삭제/추가 or 값 변경
    - 간단한 데이터 transform 에 효과적
    - 만약 document 의 크기가 크다면, 모든 document 들이 ingest pipeline 을 거치기 때문에 비효율적이 될수 있다.
    
- Machine Learning 노드
    -머신러닝 job 이 가능한 노드
  
- Coordination
    - 쿼리의 분산및 결과 집계를 위한 노드 
    - 스스로는 검색을 하지 않고, 데이터 노드에 위임 하여 결과를 모으고 조작한다.
    - 매우 큰 클러스터에 효과적이고, 로드 밸런싱 같은 역할을 한다.
    - 데이터를 모으고 조작하는 작업을 하기 때문에 CPU/메모리 리소스가 많이 필요하다.