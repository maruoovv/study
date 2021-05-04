# es-complete-guide
공식문서는 API 레퍼런스가 많고, 다른 이해를 필요로 하는게 많다.  
ES 는 복잡하고 어디서부터 공부해야할지 막연하다.  
전체 documents 를 커버하긴 힘드니까 핵심 개념들을 공부해보자.

### Elastic Search

- open source analytics & full-text search engine
- full-text search 뿐 아니라 통계 데이터, 분석을 위해 사용할수도 있다.

- Data is stored as documents
- 데이터는 도큐먼트로 저장된다. (JSON object 형태)
    - RDBMS 의 row 와 비슷한 개념이다
- 도큐먼트의 데이터는 field 로 분리된다
    - RDBMS 의 columns 와 비슷한 개념이다

### Overview of the Elastic Stack

- Kibana
    - analytics & visualization platform
    - elasticsearch 대시보드라고 생각해도 좋다
- Logstash
    - 어플리케이션의 로그를 수집해서 Elasticsearch 에 보내는 역할을 한다
    - 일반적인 의미 외에 Data processing pipeline 으로도 사용된다.
    - input, filter, output 으로 구성. 다양한 plugin 이 있다.
- X-pack
    - Secuirty (Authorization, Authentication)
    - Monitoring (CPU & memory usage, disk space, etc)
    - Alerting
    - Reporting
    - Machine Learning
    - Graph
    - Elasticsearch SQL
- Beats
    - data shippers (ex. Filebeat, Metricbeat...  )

```
X-pack : Add features to the Elastic Stack
Logstash, Beats : Data ingestion
Elasticsearch : Search, Analyze, Data store
Kibana : Visualize data
```