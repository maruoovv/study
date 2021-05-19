### Introduction to analysis

- Document 가 indexing 될때, Text value 가 analyze 된다.
- analyze 결과가 검색에 효율적인 자료구조상으로 저장된다.
- _source object 자체가 검색에 사용되지 않는다.

![img.png](img.png)

#### character filters

문자를 더하거나, 빼거나, 바꾼다. 0개 이상의 필터를 사용할수 있고, 순서를 명시할수 있다.   

#### Tokenizer

하나의 Tokenizer 가 있고, 문자열을 tokenize 한다. (splits into tokens)

#### Token Filters

tokenizer 결과를 받아 토큰에 더하거나, 제거하거나, 수정한다.  
0개 이상의 필터를 사용할수 있고, 순서를 명시할수 있다.


- 이미 정의된 여러 analyzer 가 존재하고, 커스텀하게 만들수도 있다.

#### Standard Analyzer

- character filters : none, tokenizer, token filters : lowercase
```
INPUT : I LIKE apple

character filters output : I LIKE apple
tokeninzer output : [I, LIKE, apple]
token filters output [i, like, apple]
```

#### Analyze API

- Test Standard Analyzer

```
POST /_analyze
{
  "text" : "to test TEXT 1234 !!",
  "analyzer": "standard"
}
analyzer 에 standard 대신 built-in analyzer, custom analyzer 를 사용할수도 있다.
```

결과는 다음과 같다.

```json
{
  "tokens" : [
    {
      "token" : "to",
      "start_offset" : 0,
      "end_offset" : 2,
      "type" : "<ALPHANUM>",
      "position" : 0
    },
    {
      "token" : "test",
      "start_offset" : 3,
      "end_offset" : 7,
      "type" : "<ALPHANUM>",
      "position" : 1
    },
    {
      "token" : "text",
      "start_offset" : 8,
      "end_offset" : 12,
      "type" : "<ALPHANUM>",
      "position" : 2
    },
    {
      "token" : "1234",
      "start_offset" : 13,
      "end_offset" : 17,
      "type" : "<NUM>",
      "position" : 3
    }
  ]
}
```
- token
- type : token type
- start, end offset
- position


standard analyzer 는 심볼, 공백등을 무시하기 때문에, 해당 토큰들은 결과에 나오지 않는다.  
char_filter, tokenizer, filter 를 각각 따로 명시해줄수도 있다.
``` 
POST /_analyze
{
  "text" : "to test TEXT 1234 !!",
  "char_filter": [],
  "tokenizer": "standard",
  "filter": ["lowercase"]
}
```

#### Understanding inverted indices

- 각 필드의 밸류는 각 data type 에 효율적인 자료구조에 저장된다.
- 아파치 루씬에 의해 핸들링된다. 

#### inverted indices

- term(analyzer 의 분석 결과 token) 과 documents 를 맵핑한다.
- terms 는 알파벳 순서로 정렬된다.
- inverted index 는 텀과 doc id mapping 외에도 relevance score 등도 포함할수 있다.
- 한 text field 당 하나의 inverted index 가 생긴다.
- text type 외 다른 data type 은 다른 방식으로 저장된다. (ex. BKD tree)

#### Introduction to mapping

- document 의 structure 를 정의 (field, data types..)
    - value 들이 어떻게 index 되는지도 정의
- RDBMS 의 schema 와 유사 

```
PUT /employees
{
    "mappings" : {
        "properties":{
            "id": { "type" : "integer" },
            "name" : { "type" : "text" },
            "created" : { "type" : "date" }
         }
    }
} 
```

- mapping 엔 두가지 방식 존재
    - Explicit mapping
        - index 생성시 직접 필드를 정의 
        
    - Dynamic mapping
        - ES 가 필드 맵핑을 생성해줌 
    
    - 위 두 방식을 섞어서 사용 가능
    

#### Overview of data types

- Object
    - for any JSON Object
    - Object 는 중첩될수 있다.
    - Object 는 object 자체로 저장되지는 않는다. (flatten 된다.)
    - object array 도 flatten 되기 때문에, 원래 object 에 있던 상관관계를 잃어버린다.
```json
[
  {
    "id" : 1,
    "rating" : 4
  },
  {
    "id" : 2,
    "rating" : 10
  }
] 
=> 
{
  "id" : [1,2],
  "rating" : [2,10]
}
```

따라서, id 1 을 가진 rating 5 이상을 찾고싶다. 이런 쿼리는 RDBMS 와 다르게 효율적으로 계산되지 않는다.
    
- Nested
    - object type 과 유사하지만, 상관관계를 유지한다.
    - object array 를 인덱싱 하는데 효율적이다.
    - apache lucene 은 object type 이 없는데 어떻게 저장될까?
        - nested object 는 hidden document 형태로 저장된다.
        - 검색 결과로는 나오지 않는다.
    
    
- keyword
    - exact value 매칭을 위한 필드다.
    - filtering, aggregation, sorting 을 위해 주로 사용된다.
    - full-text search 를 위해선 이 타입 말고 text 사용하자. 
      
##### 왜 full-text search 로는 사용하기 부적절할까? keyword field 가 어떻게 분석되고 저장되는지 알아보자.

- keyword field 는 keyword analyzer 가 사용된다.
- keyword analyzer 는 no-op analyzer input 그대로가 output 으로 나오게 된다.
- input 이 여러 token 으로 분리되지 않고, 전체 input 이 그대로 저장되기 때문에 (특수문자, 대/소문자 구분등도 안함..) full-text search 로는 부적절하다.
- email 같은 exact matching 을 위해서는 사용하기 적절하다.



#### Understanding arrays

ES필드의 데이터는 0개 혹은 더 많이 저장될수 있기 때문에 명시적인 array 타입이란건 없다.  
array 의 각각의 요소는 같은 data type 이기만 하면 된다.  

```
POST /products/_doc 
{
    "tags" : ["Tag1", "tag2"]
}
```

다음과 같이 Document 를 생성하더라도, 맵핑은 다음과 같이 생성된다.

```
{
    "products" : {
        "mappings": {
            "properties": {
                "tags": {
                    "type": "text"
                }
            }
        }
    }
} 
```

그렇다면 내부적으로는 어떻게 저장되는걸까?  
text 는 하나의 string 으로 합쳐져서 토큰으로 나뉘게 되고, 다른 타입들은 적절한 data structure 로 각각 저장이 된다.  

**만약 objects array 를 사용할때 object 에 대한 쿼리가 필요하다면 nested data type 을 사용해야한다. 아니라면 object 사용 **

