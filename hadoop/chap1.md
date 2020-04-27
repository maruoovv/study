## 하둡과의 만남

### 데이터 저장소와 분석 

하드 디스크의 용량은 지난 수년간 엄청자게 증가 했지만 데이터를 읽는 속도는 그에 미치지 못했다.  
단일 디스크의 데이터를 읽는 데 너무 많은 시간이 걸리고, 심지어 쓰는 것은 더 느리다.  
시간을 줄이는 확실한 방법은 여러 개의 디스크에서 동시에 데이터를 읽는 것이다.  

그러나 여러 개의 디스크에 데이터를 병렬로 쓰거나 읽으려면 몇몇 문제를 고려해야 한다.  

첫 번째 문제는 하드웨어 장애다. 많은 하드웨어를 사용할수록 장애가 발생할 확률도 높아진다.  
데이터 손실을 막기 위한 일반적인 방법은 데이터를 여러 곳에 복제하는 것이다. 

두 번째 문제는 분할된 데이터를 대부분의 분석 작업에서 어떤 식으로든 결합해야 한다는 것이다.  
즉, 하나의 디스크에서 읽은 데이터를 다른 99개의 디스크에서 읽은 데이터와 결합해야 할지도 모른다.  
많은 분산 시슽메에서 정합성을 지키는 것은 매우 어려운 도전과제다. 

### 전체 데이터에 질의하기

맵리듀스의 접근법은 brute-force 방식처럼 보인다. 맵리듀스의 전제가 한 번의 쿼리로 전체나 상당한 규모의 데이터셋을 처리히나느 것이기 때문이다.  
하지만 이것이 맵리듀스의 장점이다. 맵리듀스는 일괄 질의 처리기고, 전체 데이터셋을 대상으로 비정형 쿼리를 수행하고 합리적인 시간 내에 결과를 보여주는 능력을 갖고 있다.  

### 일괄 처리를 넘어서

맵리듀스의 강점은 기본적으로 일괄 처리 시스템이라는 것이고, 대화형 분석에는 적합하지 않다. 질의를 실행한 후 수 초 이내에 결과를 받는 것은 불가능하다.  
하둡은 최초에 일괄 처리를 위해 만들어 졌으나 지금은 진화하고 있다. 실제로 하둡이란 단어는 HDFS 와 맵리듀스 만이 아닌 수많은 에코시스템을 지칭 한다.  
하둡 에코시스템은 분산 컴퓨팅과 대규모 데이터 처리를 위한 기반 시설이다.

### 다른 시스템과의 비교 

#### 관계형 데이터베이스 관리 시스템 

왜 여러 개의 디스크를 가진 데이터베이스를 이용해 대규모 분석을 하기 힘들까?  
이는 '탐색 시간은 전송 속도보다 발전이 더디다'는 디스크 드라이브의 특성에서 찾을 수 있다.
데이터 접근 패턴이 탐색 위주라면 데이터셋의 커다란 부분을 읽거나 쓰는 작업은 전송 속도에 좌우되는 스트리밍 조작보다 더 오래 걸릴 것이다.  
반면 데이터베이스에 있는 일부 레코드를 변경하는 작업은 전통적인 B-트리(RDB 자료 구조) 가 더 적합하다.  
다음은 RDBMS 와 맵리듀스를 비교한 표이다.

||RDBMS|맵리듀스|
|---|---|---|
|데이터 크기|기가바이트|페타바이트|
|접근 방식|대화형, 일괄처리방식|일괄처리방식|
|변경|여러번 읽고 쓰기|한번 쓰고 여러번 읽기|
|구조|쓰기 수준 스키마|읽기 수준 스키마|
|무결성|높음|낮음|
|확장성|비선형|선형|

하둡과 RDBMS 의 차이는 점점 불분명해지고 있다. RDBMS 는 하둡의 개념을 포함하기 시작했으며, 하둡 시스템은 대화형으로 발전해 나가고 있다.  
하둡과 RDBMS 의 큰 차이점은 데이터셋 내부에서 처리되는 구조의 양이다.  
**정형 데이터**는 미리 정의된 스키마를 가진 데이터베이스 테이블과 같은 형식이 정의된 항목으로 구조화되어 있다. 이는 RDBMS의 특징이다.  
**반정형 데이터**는 정형 데이터에 비해 스키마가 유연하거나 생략될 수도 있다. 따라서 데이터 구조에 대한 최소한의 규칙만 있으면 된다.  
**비정형 데이터**는 어떠한 내부 구조도 없다.  
하둡은 처리 시점에 데이터를 해석하도록 설계되어 있기 떄문에 비정형 데이터나 반정형 데이터도 잘 처리할 수 있다.  
**읽기 시점 스키마** 라 불리는 이러한 특성은 유연성을 제공하고 데이터를 불러오는 비용이 많이 드는 단계도 피할 수 있다.  

관게형 데이터는 무결성을 유지하고 중복을 제거하기 위해 주기적으로 정규화 된다.  
정규화는 하둡에서 문제가 되는데, 하둡은 비지역 연산으로 레코드를 읽고, 하둡의 핵심 전제는 고속의 순차적 읽기와 쓰기를 수행하는 것이기 때문이다.

#### 그리드 컴퓨팅 

하둡은 가능하면 데이터를 계산 노드에 함께 배치한다. 따라서 데이터가 로컬에 있기 때문에 접근도 빠를 수 밖에 없다.  
**데이터 지역성**으로 알려진 이 특성이 하둡에서 데이터 처리의 핵심이고, 좋은 성능을 내는 이유이다.  

대규모 분산 컴퓨팅에서 수많은 프로세스를 조율하는 것은 엄청난 과제다.  
특히 어려운 점은 부분 실패에 현명하게 대처하는 것과 전체적인 계산의 진행을 이어가는 것이다.  
맵리듀스와 같은 분산 처리 프레임워크는 실패한 태스크를 자동으로 감지하여 장애가 없는 머신에 다시 배치하도록 구현되어 있기 때문에 개발자는 실패에 대해 크게 고민하지 않아도 된다.  
이러한 일이 가능한 이유는 맵리듀스가 태스크 간의 상호 의존성이 없는 **비공유 아키텍쳐** 이기 때문이다.