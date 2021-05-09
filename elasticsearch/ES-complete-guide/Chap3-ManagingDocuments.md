## Managing Documents

### creating & deleting indices

- remove
    - DELETE /{name}
    
- create
    - PUT /{name}
    
- create with index setting

```
PUT /{name}
{
    "settings" : {
        "number_of_shards" : 2,
        "number_of_replicas" : 2
    }
}

```

### indexing documents

- indexing documents
```
POST /{name}/_doc 
{
    "name" : "test",
    "price" : 10000
}
```

- response
```
{
  "_index" : "products",
  "_type" : "_doc",
  "_id" : "x7LhQXkBbY42VluRpWbi",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```

- indexing documents with id
- id 를 명시하려면 PUT 요청 사용
```
PUT /{name}/_doc/{id}
{
    "name" : "testwithid",
    "price" : 10000
}
```

- response
```
{
  "_index" : "products",
  "_type" : "_doc",
  "_id" : "100",
  "_version" : 2,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 1,
  "_primary_term" : 1
}
```

### get document by id

```
GET /{name}/_doc/{id}
```

### Updating documents
- document id 와 함께 POST 요청으로 update 가능

```
POST /{name}/_update/{id}
{
  "doc" : {
      "price" : 2000
  }
}
```

- 기존 필드를 수정하는것뿐 아니라 새로운 필드도 추가 가능


---

** ES 의 Document 는 immutable 하다!!**  
그럼 이전에 도큐먼트가 변경된건 무엇일까?  
도큐먼트가 변경된게 아니라 replaced 된것이다.  
ES 의 update 기능을 사용하지 않고 Application 레벨에서 Document 를 가져오고 변경할수도 있다.  
하지만 이렇게 했을때 가져올때 한번, 변경후 저장할때 한번 총 두번의 API 가 발생하므로, 그냥 ES Update 를 하는게 좋아보인다.  


### Scripted updates
- ES 는 script 기능을 제공한다. 복잡한 변경이나 조회를 필요로 할때 효과적이다.
- application 에서 특정 로직을 적용하기 편하다.
- source 에 key 를 적용한다.
- parameter 도 추가 가능하다.
```
POST /{name}/_update/{id} 
{
  "script": {
    "source" : "ctx._source.{field} = ASDF"
  }
}
```

```
POST /{name}/_update/{id} 
{
  "script": {
    "source" : "ctx._source.{field} = params.price",
    "params" : {
      "price" : 50000
    }
  }
}
```

만약 특정 조건에 따라 값을 변경시키는 스크립트가 있다고 하자.
```
POST /{name}/_update/{id} 
{
  "script": {
    "source" : """
      if (ctx._source.{field} == 0) {
        ctx._source.{field} = -1;
      }
    """
    }
  }
}
```

이런 경우에, 실제 field 값이 0이 아닐때에도 ES 응답의 result 는 updated 로 나온다.  
이걸 원치 않는다면, noop 을 사용한다.
```
POST /{name}/_update/{id} 
{
  "script": {
    "source" : """
      if (ctx._source.{field} == 0) {
        ctx.op = 'noop'
      }
    """
    }
  }
}
```

### Upsert
- update or insert
```
POST /{name}/_update/{id}
{
  "script" : {
    "source" : "ctx._source.{field}++"
  },
  "upsert" : {
    "name" : "test",
    "price" : 1000
  }
} 
```

### Delete Document

```
DELETE /{name}/_doc/{id} 
```

### Understanding routing

- ES 는 어떤 documents 가 어디에 있는지 어떻게 알까?
- 어떤 샤드에 있는지 어떻게 찾고, 조회, 수정, 삭제를 어떻게 할까?

이를 이해하려면 routing 이란 개념을 알아야 한다.
**Routing** 은 document 가 어느 샤드에 있는지 알아내고, 또한 어느 샤드에 처음 저장되어야 하는지를 결정하는 과정이다.
ES 가 document 를 탐색, 저장할때, 어떤 샤드에 저장할지는, 어느 샤드에 있는지는 간단한 공식을 통해 계산된다.

```
shard_num = hash(_routing) % num_primary_shards 
```

_routing 의 기본값은 document id 이다.  
ES 의 routing 은 customizing 할수있다.

### ES Read Data

single document 조회 가정.  

GET /{index}/_doc/{id} (Read request)  
=> Node X (coordinating node) 요청을 coordinating node 가 받는다.  
=> figure out where document stored (routing) Routing 은 document's 의 replication group 을 선택한다.  
Adaptive Replica Selection(ARS) 는 최선이라고 생각되는 샤드를 선택한다.   
요청을 처리하고 반환한다.


### ES Write data
PUT /{index}/_doc/{id}  

- High level 에서 바라본 과정
routing 과정은 동일하다.  
write request 는 무조건 primary shard 로 routing 된다.  
primary shard 는 요청을 validating 하는 책임을 가진다.  
primary shard 는 이후 먼저 write 요청을 locally 로 수행하고, replica shard 에 forwarding 한다.  
만약 replicated 가 실패하더라도 요청은 정상 성공 반환된다.
  
### ES 가 data replication 실패를 다루는 과정  
ES 는 분산 시스템이고 많은 operation 이 비동기로 일어나기 때문에, 많은 실패가 일어날 수 있다.  
  
ES 는 이를 위해 Primary terms, Sequence number 를 사용한다.
#### Primary terms
- replication 그룹 내에 primary shard 가 바뀌었을때, 어느게 최신인지 판단하는 방법
- primary shard 가 몇번 바뀌엇는지 기억하는 counter
- 모든 rep group 의 primary terms 는 클러스터에 저장

#### Sequence number
- write operation 에 primary term 과 함께 덧붙여진다.  
- primary shard 가 sequence number 를 증가시키는 책임을 갖고있다.
- 더 오래된 write operation 이 어떤건지 알수 있게 해준다.

```
primary terms, sequence number 를 이용해 primary shard fail 이 발생했을때 복구할 수 있다.  
디스크에 있는 데이터를 비교하는 것 보다, 
primary terms, sequence number 를 이용해 이미 수행된 요청이 무엇인지, 복구해야 할 요청은 무엇인지 알수 있다.
하지만 매우 큰 인덱스라면, 이 과정은 매우 비용이 크다.
이 비용을 줄이기 위해 ES 는 global and local checkpoint 를 사용한다.
```

#### Global and Local checkpoints
- TODO : 이 부분은 잘 이해가 안됨.
- sequence number 와 유사하다.
- Rep group 은 global checkpoint, Replica shard 는 local checkpoint 이다.