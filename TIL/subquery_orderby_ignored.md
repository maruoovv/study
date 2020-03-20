## mysql subquery order by and group by

이 결과는 mysql5.7 에서 테스트 했습니다. mysql5.6 에선 재현되지 않았습니다.  
 
mysql 쿼리가 기대와 다른 결과가 나왔다.  

```
SELECT concat(res.col_1, res.col_2)
  FROM (
  		SELECT a.col_1 as col_1,
  			   a.col_2 as col_2,
  			   a.col_3 as col_3
  		  FROM test a
  		 ORDER BY a.col_2 ASC
  ) res
GROUP BY res.col_1
```

FROM 절의 서브쿼리에서 a.col_2 로 정렬을 하고, a.col_1 로 GROUP BY 조건을 걸었다.  
기대한 결과는 FROM 절 서브쿼리에서 col_2 로 정렬이 되고, 외부에서 col_1 로 그룹바이를 하니,  
subquery 에서 정렬된 col_1의 각 첫 번째 행이 뽑혀져 나올 것으로 기대를 했다.  

하지만 생각대로 나오지 않았다... 

그 이유를 좀 찾아보니..   
subquery 의 order by 는 dbms 의 옵티마이저가 필요 없다고 판단하여 지운다고 한다.  
https://mariadb.com/kb/en/why-is-order-by-in-a-from-subquery-ignored/  
```
A "table" (and subquery in the FROM clause too) is - according to the SQL standard - 
an unordered set of rows. Rows in a table (or in a subquery in the FROM clause) do not come in any specific order
```
subquery 의 결과는 임시 테이블로 만들어 지는데, 이 임시 테이블은 내부 정렬 결과를 보장하지 못한다.  

subquery 의 ordering 결과를 유지하려면, LIMIT 을 주어 해결할 수 있다.

```
SELECT concat(res.col_1, res.col_2)
  FROM (
  		SELECT a.col_1 as col_1,
  			   a.col_2 as col_2,
  			   a.col_3 as col_3
  		  FROM test a
  		 ORDER BY a.col_2 ASC
  		 LIMIT N
  ) res
GROUP BY res.col_1
```

하지만 이게 좋은 방법인지에 대한 의문이 있다.. 대용량 데이터이고 실시간 app이라면?  
쿼리를 바꾸던지, 그룹핑을 어플리케이션에서 처리 하는게 더 좋아 보인다.  


