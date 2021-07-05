## Searching for a term

```

GET /test_index/_search 
{
  "query" : {
    "term": {
      "tags": {
        "value": "computer"
      }
    }
  }
}

// briefly
GET /test_index/_search 
{
  "query" : {
    "term": {
      "tags": "computer"
    }
  }
} 
```

### Searching for multiple terms

```
GET /test_index/_search 
{
  "query" : {
    "terms": {
      "tags": {
        "computer",
        "samsung"
      }
    }
  }
} 
```

### get documents based on IDs

```
GET /test_index/_search 
{
  "query" : {
    "ids": {
      "values": [1,2,3]
    }
  }
} 
```

### matching documents with range values
```
GET /test_index/_search 
{
  "query" : {
    "range": {
      "in_stock": {
        "gte": 1,
        "lte": 5
      }
    }
  }
} 
```

```
GET /test_index/_search 
{
  "query" : {
    "range": {
      "created": {
        "gte": "2020/01/01",
        "lte": "2020/12/31"
      }
    }
  }
} 
```

### Matching documents with non-null values
```
// get tags field not null
{
  "query" : {
    "exists" : {
        "fields" : "tags"
    }
  }
} 
```

### Matching based on prefix

```
{
  "query" : {
    "prefix": {
      "tags.keyword": "Vege"
    }
  }
} 
```           