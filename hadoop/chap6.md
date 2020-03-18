## 맵리듀스 프로그래밍

실제로 맵리듀스 잡을 만들어 내는 과정을 살펴보자.

1. 단위 테스트 작성
2. 로컬에서 잡 실행
3. 클러스터에서 실행
4. 잡디버깅/튜닝 

보통 이 순서로 이루어진다.   
책의 예제는 단순한 데이터 처리이지만, 데이터 처리가 더 복잡해지면 복잡한 맵, 리듀스 함수를 만드는 것보다 맵리듀스 잡을 더 많이 만드는 것이 좋다.  
**잡을 복잡하게 만드는 것보다는 잡을 더 많이 만드는게 좋다**
복잡한 문제는 맵리듀스 대신 고수준 프레임워크(하이브, 스파크 등) 을 이용하는 것이 더 좋다

### 엠알유닛으로 단위 테스트 작성하기
맵리듀스의 맵과 리듀스 함수는 개별적으로 테스트 하기 쉽다.  
MRUnit은 매퍼와 리듀서에 데이터를 전달하고 예상대로 출력되는지 점검할 수 있는 라이브러리다.

#### 매퍼

```java
public class CustomMapperTest {
	@Test
	public void processMapper() throws Exception {
		Text value = new Text("test:1");
		
		new MapDriver<LongWritable, Text, Text, IntWritable>()
		.withMapper(new CustomMapper())
		.withInput(new LongWritable(0), value)
		.withOutput(new Text("test"), new IntWritable("1"))
		.runTest();
	}
}
```

MRUnit의 MapDriver를 사용해, 입력키와 값, 예상되는 출력키와 값을 설정한 다음 runTest() 함수를 호출한다.  
매퍼의 출력값이 예상과 다르면 MRUnit 테스트는 실패한다.

리듀서도 같은 방식으로 테스트 코드를 작성할 수 있다.

```java
public class CustomReducerTest {
	@Test
	public void reducerTest() throws Exception {
		new ReduceDriver<Text, IntWritable, Text, IntWritable>()
		.withReducer(new CustomReducer())
		.withInput(new Text("test"), Arrays.asList(new IntWritable(1), new IntWritable(10))
		.withOutput(new Text("test"), new IntWritable(10))
		.runTest());
	}
}
```


### 로컬에서 실행하기 
잡 드라이버를 작성하고 개발 머신에서 테스트 데이터로 실행해보자.  

#### 로컬 잡 실행하기 
Tool 인터페이스를 활용하여 맵리듀스 잡을 실행하는 드라이버를 쉽게 작성할 수 있다.
```java
public class JobDriver extends Configured implements Tool {
	
	@Override
	public int run(String[] args) throws Exception {
		Job job = new Job(getConf(), "Test");
		job.setJarByClass(getClass());
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputformat.addOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(CustomMapper.class);
		job.setCombinerClass(CustomCombiner.class);
		job.setReducerClass(CustomReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static void main(String[] args)  throws  Exception{
	  int exitCode = ToolRunner.run(new JobDriver(), args);
	  System.exit(exitCode);
	}
}
```

run() 메서드는 Tool 의 환경 설정을 기반으로 Job 객체를 생성한다.  
입출력 파일 경로, 매퍼, 리듀서, 컴바이너, 출력 타입을 설정했다.  
환경설정에서 mapreduce.framework.name 속성을 local 로 설정하면 로컬 잡 실행자가 사용된다.  
이 실행자는 테스트 용도로 설계되어 매퍼와 리듀서 코드를 단계별로 실행하며 디버깅 할 수 있다.

### 클러스터에서 실행하기 
하둡 클러스터에서 전체 데이터셋으로 실행해보자.  

#### 잡 패키징 
로컬 잡 실행자는 잡을 실행할 때 단일 JVM 을 사용하므로 잡에 필요한 클래스들의 로컬의 클래스경로에 존재하면 잘 동작한다.  
하지만 분산환경은 잡을 시작할 때 필요한 모든 클래스를 잡 Jar 파일에 패키징해서 클러스터로 보내야 한다.  
잡 jar 파일은 빌드 도구를 이용해 쉽게 생성할 수 있다.  

#### 잡 구동하기 
잡을 구동하기 위해 드라이버를 실행할 때 -conf 옵션으로 잡을 실행할 클러스터를 지정해야 한다. 
```
hadoop jar hadoop-examples.jar CustomDriver \ -conf conf/hadoop-cluster.xml input/test
```

#### 원격 디버깅 
어떤 태스크가 실패했을 때 이 에러를 진단하기 위한 로그가 충분하지 않다면 어떻게 해야할까?  
클러스터에서 잡을 싱핼할 때는 어느 노드가 입력 데이터셋의 어떤 부분을 처리할지 모르기 때문에 디버거를 설정하기엔 어려움이 있다.  
이럴때 다음과 같은 방법을 활용한다.

- 로컬에서 실패 재현
	특정입력 데이터에 대해 지속적으로 실패가 발생하면, 그 파일을 로컬에 받은 후 잡을 실행하여 재현한다.
- JVM 디버깅 옵션
	시랲의 주된 원인은 자바의 메모리 부족 때문이다. HeapDumpOnOutOfMemory 옵션을 주어, OOM 발생 시 힙 덤프 정보를 남기도록 하여 후에 분석할 수 있게 해준다.
- 태스크 프로파일링 사용
	자바 프로파일러는 JVM에 대한 통찰력을 제공하며, 하둡은 잡의 일부 태스크에 대한 프로파일링 기법을 제공한다.
	
### 잡 튜닝하기
잡을 튜닝 할때 당므과 같은 목록을 점검해보는게 좋다. 

|영역|모범사례|추가정보|
|---|---|---|
|매퍼 수|매퍼가 얼마나 오랫동안 수행되고 있는가? 만약 평균 몇 초 내로 수행된다면 더 적은 수의 매퍼로 더 오래 실행할 수 있는지(1분 내외) 확인해보자.||
|리듀서 수|두 개 이상의 리듀서를 사용중인지 확인하자. 리듀스 태스크는 5분 내외로 실행되며,  최소 하나의 의미 있는 데이터 블록을 만들어야 한다.||
|컴바이너|셔플을 통해 보내지는 데이터양을 줄이기 위해 컴바이너를 활용 할 수 있는지 확인해보자.||
|중간 데이터 압축|맵 출력을 압축하면 잡 실행 시간을 많이 줄일 수 있다.||
|커스텀 직렬화|커스텀 직렬화 객체나 비교기를 사용한다면 RawComparator 를 구현했는지 확인해라.||
|셔플 튜닝|맵리듀스 셔플은 메모리 관리를 위해 약 12개의 튜닝 인자를 제공한다.||
