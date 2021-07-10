### Full Text Query


#### match query

```
GET /recipe/_search
{
    "query": {
        "match" : {
            "title" : "Recipes with pasta or spaghetti"
        }
    }
} 
```

기본적으로 matching 쿼리는 텀으로 구분되어, boolean query 로 작동한다.
위의 예제는 Recipes, with, pasta, or, spaghetti 의 term 으로 분리되고 이중 하나라도 있다면 결과에 나오게 된다.  


만약 이 옵션을 모든 term 을 포함하는 검색을 하고 싶다면 다음과 같이 하면 된다.
```
GET /recipe/_search
{
    "query": {
        "match" : {
            "title" : {
                "query": "Recipes with pasta or spaghetti",
                "operator": "and"
            }
        }
    }
} 
```

#### matching phrase
만약 단어가 아니고 문장을 검색하고, 문장의 순서도 일치해야 한다면 
match_phrase 를 사용할수 있다.

```
GET /recipe/_search
{
    "query": {
        "match_phrase" : {
            "title" : "pasta or spaghetti"
        }
    }
} 
```


#### searching multiple fields

동일한 쿼리를 여러 필드에 대해 검색하고 싶다면 다음과 같이 할수 있다.

```
GET /recipe/_search
{
    "query": {
        "multi_match": {
            "query": "pasta",
            "fields": ["title", "description"]
        }
    }
} 
```
