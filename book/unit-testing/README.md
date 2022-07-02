Unit Testing (단위 테스트)


## Chapter1
좋은 단위 테스트를 작성하는 것은 어떤 의미일까?  
많은 프로젝트에 자동화된 테스트가 있고, 테스트도 많다. 하지만 테스트가 있더라도 개발자들이 원하는 결과를 얻지 못하는 경우가 많다.  
프로젝트를 개발하는데 오래 걸리고, 이미 구현된 기능에 버그가 지속적으로 나타난다.  
이는 제대로 작동하지 않는 단위 테스트라면 누구나 처할수 있는 상황이다.

### 단위 테스트의 목표
소프트웨어의 지속 가능한 성장을 가능하게 하는 것이다.  
테스트가 없는 프로젝트는 처음에는 빨리 시작할 수 있지만 시간이 지나면서 점점 더 많은 시간을 들여야 처음과 같은 정도의 속도를 낼 수 있다.  
개발 속도가 빠르게 감소하는 이런 현상을 소프트웨어 엔트로피 라고도 부른다.  
지속적인 정리와 리팩터링 등과 같은 적절한 관리를 하지 않고 방치하면 소프트웨어는 점점 더 복잡해지고 무질서해진다.  
하나의 버그를 수정하면 더 많은 버그가 생기고 한 부분을 수정하면 다른 부분들이 망가진다.  
테스트는 이러한 문제들에 대해 안전망 역할을 하여 새로운 기능을 도입하거나 새로운 요구사항에 맞게 리팩터링한 후에도 기존 기능이 잘 작동하는지 확인하는데 도움이 된다.  
테스트를 작성하는데 한가지 단점은, 초반에 상당한 노력이 필요하다는 것이다.

#### 좋은 테스트와 좋지 않은 테스트
잘못 작성한 테스트는 테스트가 없는 것과 같은 결과를 낳는다.

#### 성공적인 테스트를 위한 조건

- 개발 주기에 통합돼 있다.
- 코드베이스에서 가장 중요한 부분만을 대상으로 한다.
- 최소 유지비로 최대 가치를 끌어낸다.

## Chapter3


### 단위 테스트를 구성하는 방법

1. AAA 패턴 사용
AAA 패턴은 테스트를 준비, 실행, 검증 세 부분으로 나눌 수 있다.  
   흔히 사용하는 Given-When-Then 패턴과 유사하다.
```java 
public void calculatorTest() {
    // 준비
    int first = 10;
    int second = 20;
    Calculator cal = new Calculator();
    
    // 실행
    int result = cal.sum(first, second);
    
    // 검증
    assertThat(result).isEqualTo(30);
    
}
```



2. 여러개의 준비, 실행, 검증 구절 피하기
때로 준비 -> 실행 -> 검증 -> 더 실행 -> 다시 검증 과 같은 테스트를 볼수 있다.  
이러한 테스트는 더이상 단위테스트라 할수 없고 통합 테스트로 봐야 한다.  
이러한 테스트 구조는 피하는 것이 좋다.


3. 테스트 내 if 피하기 
테스트 문 내에 if 문이 있는 것도 안티 패턴이다.  
단위 테스트든 통합 테스트든 테스트는 분기가 없는 간단한 일련의 단계여야 한다.  
   
4. 각 구절의 크기
일반적으로 준비 구절이 가장 크며, 실행과 검증을 합친 만큼 클수도 있다.  
실행 구절이 한 줄 이상인 경우 코드 캡슐화가 깨진 증거일수 있기 떄문에 경계해야 한다.  

5. 검증 구절의 검증문
단일 메소드 실행은 여러 결과를 낼수 있으며, 하나의 테스트는 그 모든 결과를 검증하는 것이 좋다.  
하지만 검증 구절이 너무 커지는 것은 경계해야 한다.
   
6. 종료 단계
준비, 실행, 검증 이외에 종료 구절을 구분하기도 한다.  
테스트에 의해 생성된 파일을 지우거나, 데이터베이스 연결을 종료하는 등이 있다.  
대부분의 단위테스트는 프로세스 외부에 종속적이지 않으므로 종료 구절이 필요 없다. 종료는 통합테스트의 영역이다. 
   

### 단위 테스트 명명법 
테스트에는 표현력이 있는 이름을 붙이는 것이 중요하다.  
가장 유명하지만 도움이 되지 않는 방법은 다음과 같다.  

`[테스트대상메서드]_[시나리오]_[예상결과]`

애플리케이션의 동작 대신에 세부 사항에 집중하게끔 하기 때문에 도움이 되지 않는다. 
복잡한 동작에 대해 높은 수준의 설명을 정책에 다 담을수 없기 때문에 표현의 자유를 허용하고,  
문제 도메인에 익숙한 비개발자들에게 시나리오를 설명하는 것처럼 테스트 이름을 짓자.



## Chapter4 

### 좋은 단위 테스트의 4대 요소

좋은 단위 테스트에는 다음 네가지 특성이 있다.
- 회귀 방지
- 리팩터링 내성
- 빠른 피드백
- 유지 보수성

1. 회귀 방지
회귀는 코드를 수정한 후 기능이 의도한 대로 작동하지 않는 경우다.   
이런 경우는 코드가 커질수록 가능성이 더 높이지기 떄문에, 회귀에 대해 효과적인 보호를 하는것이 중요하다.  
이러한 보호가 없다면 프로젝트는 오랫동안 성장할수 없으며 점점 더 많은 버그가 쌓일 것이다.  
회귀 방지를 하려면 테스트가 가능한 많은 코드를 실행하며, 도메인의 핵심적인 로직을 많이 테스트 하는 것이 좋다.  
   

2. 리팩터링 내성
테스트가 실패하지 않고 기존 애플리케이션 코드를 리팩터링 할수 있는지에 대한 지표다.  
애플리케이션 코드의 입출력은 바뀌지 않았지만 리팩터링을 한 후에 테스트가 실패 한다면 기능은 의도한 대로 작동하지만 테스트는 실패하는 것이 된다.  
이러한 상황을 거짓 양성이라고 한다. 거짓 양성이 많아지만 전체 테스트 코드에 치명적인 영향을 줄 수 있다.
테스트가 지속 가능한 성장을 하게 하는 이유는 회귀 없이 주기적으로 리팩터링하여 새로운 기능을 추가할 수 있는 것인데, 거짓 양성은 이러한 장점을 방해한다.
   
> 거짓 양성의 원인  
테스트가 테스트 대상 시스템의 구현 세부 사항과 많이 결합할수록 거짓 양성이 더 많이 생긴다.  
이를 줄이는 방법은 구현 세부 사항에서 테스트를 분리하는 방법이 있다.  
테스트는 사용자 관점에서 시스템을 검증해야 하고, 사용자에게 의미 있는 결과만 검증해야 한다.

3. 빠른 피드백과 유지 보수성
빠른 피드백은 단위 테스트의 필수 요소이다. 빠를수록 더 많은 테스트를 수행할 수 있고 더 자주 실행할 수 있다.  
느린 테스트는 피드백을 느리게 하고 잠재적 버그를 뒤늦게 발견하여 버그 수정 비용이 증가한다.
유지보수성은 테스트가 얼마나 이해하기 어려운가, 얼마나 실행하기 어려운가 라는 두 가지 주요 요소로 구성된다.
   
    - 얼마나 이해하기 어려운가   
    테스트는 코드 라인이 적을 수록 더 읽기 쉽고 변경하는 것도 쉽다.
    테스트 코드의 품질은 애플리케이션 코드 만큼 중요하다  
    - 테스트가 얼마나 실행하기 어려운가  
    테스트가 외부 종속성으로 작동하면 DB 서버를 재부팅하고 네트워크 문제를 해결하는 등의 시간을 들여야 할 수 있다.

### 이상적인 테스트를 찾아서
안타깝게도 위의 네 조건 모두를 극대화 하는 테스트를 만들기는 어렵다.  
유지 보수성은 나머지 세 특성과 상관관계가 없지만, 나머지 세 특성의 경우에는 특정 특성의 점수를 극대화 하면, 다른 특성의 점수는 내려간다.  
실제로는 리팩터링 내성을 포기할 순 없기 떄문에, 테스트가 얼마나 버그를 잘 찾아내는지 (회귀 방지)와 얼마나 빠른지(빠른 피드백) 사이의 선택으로 절충된다.  

테스트는 크게 엔드 투 엔드 테스트, 통합 테스트, 단위 테스트로 나눌수 있는데  
단위 테스트의 경우에는 빠른 피드백을 더 중점에 두고, 엔드 투 엔드 테스트로 갈수록 회귀 방지에 더 중점을 두는 것이 일반적일 것이다.  
테스트 유형 간의 비율은 다 다를테지만, 일반적으로 단위 테스트 > 통합 테스트 > EtoE 테스트 로 피라미드 형태를 유지하는 것이 좋다.  

엔드투엔드 테스트는 빠른 피드백이 불가능하고, 유지 보수성이 낮기 떄문에 가장 중요한 기능에 적용하는 것이 좋다.

## Chapter5

목의 사용에 대해서는 논란의 여지가 있다.  
목이 취약한 테스트를 초래하는 경우도 있지만, 목 사용이 바람직한 경우도 있다.

### 목과 스텁
테스트 대역은 테스트 대상 시스템과 협력자 사이의 상호 작용을 대체할 수 있는 것이다.  
주로 목과 스텁으로 나누어 지는데 두 유형의 차이점은 다음과 같다.

- 목은 외부로 나가는 상호 작용을 모방하고 검사하는데 도움이 된다.
- 스텁은 내부로 들어오는 상호 작용을 모방하는데 도움이 된다.

목은 테스트 대상 시스템과 관련 의존성 간의 상호 작용을 모방하지만, 스텁은 모방만 한다.

목과 스텁의 개념은 명령 조회 분리 원칙 (CQRS) 과 관련이 있다.  
CQRS 원칙에 따르면 모든 메서드는 명령이거나 조회여야 한다.  
명령을 대체하는 테스트 대역은 목이고, 조회를 대체하는 테스트 대역은 스텁이다.  
목과 스텁을 구분하지만 한 테스트 코드 내에서 혼재하여 사용하기도 한다.


### 식별할수 있는 동작과 구현 세부 사항
테스트가 거짓 양성을 발생시키는 주요 이유는 구현 세부 사항과 결합돼 있기 때문이다.  
이런 결합을 피하는 방법은 코드가 생성하는 최종 결과를 검증하고 구현 세부 사항과 테스트를 떨어뜨리는 것 뿐이다.  
즉 테스트는 __어떻게__ 가 아니라 __무엇__ 에 중점을 둬야 한다. 
이는 테스트만의 문제가 아닌 구현 상의 문제와도 연관이 있다.  
클래스가 구현 세부 사항을 유출한다면 결합될 가능성이 높다.  
단일한 목표를 달성하고자 동일한 클래스에 호출하는 연산의 수가 1보다 크면 구현 세부 사항이 유출되어 있을 가능성이 있다.



## Chap7. 가치 있는 단위 테스트를 위한 리팩터링

### 리팩터링할 코드 식별하기

#### 코드의 네가지 유형

모든 코드는 2차원으로 분류할 수 있다. 

- 복잡도 또는 도메인 유의성
- 협력자 수

코드 복잡도는 코드 내 분기 지점수로 정의하고, 이 숫자가 클수록 복잡도는 높아진다.  
도메인 유의성은 코드가 프로젝트의 문제 도메인에 대해 얼마나 의미있는지를 나타낸다.  

복잡한 코드와 도메인 유의성을 갖는 코드가 단위 테스트에서 가장 중요하다. 

두번째 차원은 클래스 또는 메서드가 가진 협력자 수다.  
협력자가 많은 코드는 테스트 비용이 많이 든다.

테스트코드를 작성할때 가장 문제가 되는 유형은 지나치게 복잡한 코드이다.  
단위 테스트가 어렵지만, 테스트 없이 그냥 내버려 두는것은 굉장히 위험하다. 
지나치게 복잡한 코드를 제거하는 것은 쉬운 일은 아니지만 도움이 되는 기법이 있다. 이 기법에 대해 알아보자.

#### 험블 객체 패턴 
험블 객체 패턴을 사용해 지나치게 복잡한 코드를 쪼갤수 있다.   

지나치게 복잡하여 테스트가 어려운 코드는 종종 테스트하기 어려운 의존성을 포함하고 있는 경우가 많다. (비동기 실행, 멀티스레드, 인터페이스, 외부 의존성 등..)  
이런 테스트하기 어려운 의존성과 실제 테스트 해야 하는 로직을 추출할수 있는데, 결과적으로 테스트 가능한 부분 (로직) 과 테스트 하기 어려운 의존성을 갖고 있는 험블 객체가 만들어 진다.  

이 패턴은 단일 책임 원칙을 지키는 것으로도 설명할 수 있다.  
클래스가 단일한 책임을 가지게 분리하다 보면, 비즈니스 로직을 테스트가 어려운 의존성과 분리할 수 있다.  
이는 상당히 익숙한 개념인데, 잘 알려진 MVC 패턴도 이 패턴과 유사하게 비즈니스 로직과 인터페이스를 분리한다.  


## Chap8. 통합 테스트를 하는 이유

단위 테스트에만 전적으로 의존하면 시스템이 전체적으로 잘 동작하는지 확신할 수 없다.  

### 통합 테스트는 무엇인가? 

#### 통합 테스트의 역할

통합테스트에 대해 알아보기에 앞서 단위 테스트에 대해 다시 복기하자.  
단위테스트는 다음 세 가지 요구 사항을 충족하는 테스트다.

- 단일 동작 단위를 검증하고
- 빠르게 수행하고
- 다른 테스트와 별도로 처리한다.

위 요구사항을 하나라도 만족하지 못하면 통합 테스트라 할수 있다.  
단위 테스트는 도메인 모델을 다루지만, 통합 테스트는 프로세스 외부 의존성과 도메인 모델을 연결하는 코드를 확인한다.  
통합테스트는 가장 긴 주요 흐름과 단위 테스트로는 수행 불가능한 예외 사항들을 다룬다.

#### 어떤 외부 의존성을 직접 테스트해야 하는가?

통합 테스트는 외부 의존성과 어떻게 통합하는지를 검증한다.  
이러한 검증을 구현하는 방식은 두가지가 있다.  
실제 프로세스 외부 의존성을 사용하거나 해당 의존성을 목으로 대체하는 것이다.  
이 두가지 방식을 언제 적용해야 하는지 알아본다.  


##### 외부 의존성의 두가지 유형

모든 프로세스 외부 의존성은 두 가지 범주로 나눌수 있다.

- 관리 의존성(전체를 제어할 수 있는 외부 의존성)
    - 애플리케이션을 통해서만 접근할 수 있으며, 해당 의존성과의 상호 작용은 외부 환경에서 볼 수 없다
    - 데이터베이스 등
- 비관리 의존성(전체를 제어할 수 없는 외부 의존성)
    - 해당 의존성과의 상호 작용을 외부에서 볼수 있다.
    - SMTP, 메시지 버스 등
    
관리 의존성은 애플리케이션을 통해서만 접근하므로 구현 세부 사항이라고 볼 수 있다.  
따라서 하위 호환성을 유지할 필요가 없고, 외부 클라이언트는 데이터베이스를 어떻게 구성하는지 관심이 없다.  
가장 중요한 관심사는 시스템의 최종 상태다. 따라서 관리 의존성은 실제 인스턴스를 사용한다.  

비관리 의존성은 하위 호환성을 지키기 위해 통신 패턴을 유지해야 한다. 따라서 목으로 대체한다. 


### 의존성 추상화를 위한 인터페이스 사용 
단위 테스트 영역에서 가장 많이 오해하는 주제 중 하나는 인터페이스 사용이다.  

#### 인터페이스와 느슨한 결합  
많은 개발자가 외부 의존성을 위해 인터페이스를 도입한다.  
다음과 같은 비슷한 클래스와 인터페이스 쌍을 자주 볼 수 있다.

interface IMessageBus
class MessageBus : IMessageBus

이렇게 인터페이스를 사용하는 일반적인 이유는

- 외부 의존성을 추상화해 느슨한 결합을 달성하고
- 기존 코드를 변경하지 않고 새로운 기능을 추가해 OCP 원칙을 지킬수 있다.  

이지만 이 두 가지 이유 모두 오해다.  
단일 구현을 위한 인터페이스는 추상화가 아니며, 해당 인터페이스를 구현하는 구체 클래스보다 결합도가 낮지 않다.  
**진정한 추상화는 발견하는 것이지 발명하는 것이 아닌다.**  
의미상 추상화가 이미 존재하지만 코드에서 아직 명확하게 정의되지 않았을 때 그 이후에 발견되는 것이다.  

두번째 이유는 더 기본적인 원칙인 YAGNI (You aren't gonna need it) 을 위한하기 때문에 잘못된 생각이다.  
YAGNI 는 현재 필요하지 않은 기능에 시간을 들이지 말라는 것이다.  
이러한 향후 기능이 어떤지 설명하려고 기능을 개발해서도, 기존 코드를 수정해서도 안된다. 이에는 크게 두가지 이유가 있다. 

- 기회 비용 : 현재 필요하지 않은 기능에 시간을 보낸다면 지금 당장 필요한 기능을 제치고 시간을 허비하는 것이다.  
실제 필요에 따라 기능을 구현하는 것이 좋다.
  
- 프로젝트 코드가 적을수록 좋다. 요구 사항이 바로 있는 경우가 아닌데도 만일을 위해 코드를 작성하면 코드베이스의 소유 비용이 불필요하게 증가한다. 
  가능한 한 새로운 기능의 도입을 미루는 것이 좋다.
  
> 코드를 작성하는 것은 문제를 해결하는 값비싼 방법이다. 해결책에 필요한 코드가 적고 간단할수록 더 좋다.


그렇다면 인터페이스에 구현이 하나만 있다고 가정할 때 외부 의존성에 인터페이스를 사용하는 이유는 무엇인가?  
간단히 말하자면 목을 사용하기 위함이다.  
인터페이스가 없으면 테스트 대역을 만들 수 없으므로 테스트 대상 시스템과 외부 의존성 간의 상호 작용을 확인할 수 없다.  
따라서 이 의존성을 목으로 처리할 필요가 없는 한, 외부 의존성에 대해 인터페이스를 두지 말자.  
간혹 내부 의존성에 대해 구현이 하나뿐인 인터페이스를 작성할 때가 있는데 이러한 인터페이스는 목을 사용하게 될 가능성이 있고,  
목을 사용해 클래스 간의 상호 작용을 확인하기 때문에 테스트가 코드의 구현 세부 사항에 결합된다. 따라서 사용하지 말자.


### 통합 테스트 모범 사례

통합 테스트를 최대한 활용하는데 도움이 되는 지침들이 있다. 

#### 도메인 모델 경계 명시하기

도메인 모델은 프로젝트가 해결하고자 하는 문제에 대한 도메인 지식의 모음이다.  
도메인 모델에 명시적 경계를 지정하면 코드의 해당 부분을 더 잘 보여주고 더 잘 설명할 수 있다.  
단위 테스트는 도메인 모델과 알고리즘을 대상으로 하고 통합 테스트는 컨트롤러를 대상으로 한다.  
도메인 클래스와 컨트롤러 사이의 명확한 경계로 단위 테스트와 통합 테스트의 차이를 쉽게 구별할 수 있다.

#### 계층 수 줄이기 

애플리케이션에 추상 계층이 너무 많으면 코드베이스를 탐색하기 어렵고 이해하기가 어려워진다.  
추상화가 지나치게 많으면 단위 테스트와 통합 테스트에도 도움이 되지 않는다.  
간접 계층이 많은 코드베이스는 컨트롤러와 도메인 모델 사이에 명확한 경계가 없는 편이고 각 계층을 따로 검증하는 경향이 강하다.  
이러한 경향으로 인해 통합 테스트는 가치가 떨어지며, 각 테스트는 특정 계층의 코드만 실행하고 하위 계층은 목으로 처리한다.  

가능한 한 간접 계층을 적게 사용하라. 대부분의 백엔드 시스템은 도메인 모델, 애플리케이션 서비스 계층, 인프라 게층 이 세가지로 할수 있다.

#### 순환 의존성 제거하기  

추상 계층이 너무 많은 것과 마찬가지로 순환 의존성은 코드를 읽고 이해하려고 할 때 알아야 할 것이 많아 부담이 된다.  
또한 순환 의존성은 테스트를 방해한다.  
순환 의존을 사용하지 않고 일반 값으로 리턴하게 하자.  

#### 테스트에서 다중 실행 구절 사용 

테스트에서 두 개 이상의 준비나 실행 또는 검증을 하는 것은 코드 악취에 해당한다.  
각 실행을 고유의 테스트로 추출해 테스트를 나누는 것이 좋다.  

이 지침의 예외적으로 원하는 상태로 만들기 어려운 외부 의존성으로 작동하는 테스트가 있다.  
이러한 경우 여러 동작을 하나의 테스트로 묶어 문제가 있는 외부 의존성에 대해 상호 작용을 줄이는 것이 유리하다.  

외부 의존성을 관리하기 어려운 경우를 제외하고는 실행 구절이 여러개 있으면 안된다.  
단위 테스트는 외부 의존성으로 작동하지 않기 때문에 절대로 여러개 구절이 있으면 안된다. 

## Chap 11. 단위 테스트 안티 패턴

### 비공개 메서드 단위테스트 
널리 알려진 안티 패턴으로 비공개 메서드에 대한 단위 테스트가 있다.

#### 비공개 메서드와 테스트 취약성
단위 테스트를 하려고 비공개 메서드를 노출하는 것은 기본 원칙중 하나인 식별할 수 있는 동작만 테스트 하는것을 위반한다.  
비공개 메서드를 노출하면 테스트가 구현 세부 사항과 결합되고 결과적으로 리팩터링 내성이 떨어진다.  
비공개 메서드를 직접 테스트하는 대신 포괄적인 식별할 수 있는 동작으로서 간접적으로 테스트 하는 것이 좋다. 

#### 비공개 메서드와 불필요한 커버리지
때로 비공개 메서드가 너무 복잡혀서 포괄적인 테스트로 충분한 커버리지를 얻을 수 없는 경우가 있다.  
비공개 메서드가 복잡하면 별도의 클래스로 도출해야 하는 추상화가 누락됐다는 징후로 볼 수 있다.  

#### 비공개 메서드 테스트가 타당한 경우
비공개 메서드를 테스트 하기 타당한 경우가 있다.  
책에서는 이 예를 ORM 을 사용하는 클래스를 든다.

```C#
public class Inquiry {
    public bool IsApproved {get; private set;}
    public DateTime? TimeApproved {get; private set;}
    
    private Inquiry (bool isApproved, DateTime? timeApproved) {
        if (isApproved && !timeApproved.HasValue) throw new Exception();
        
        IsApproved = isApproved;
        TimeApproved = timeApproved;
    }
    
    public void Approve(DateTime now) {
        if (IsApproved) return;
        
        IsApproved = true;
        TimeApproved = now;
    }
} 
```

해당 ORM 라이브러리는 공개 생성자가 필요하지 않으며, 시스템은 이 조회를 만들어낼 책임이 없기 때문에 생성자가 필요하지 않다.  
하지만 이 객체의 도메인 로직은 분명히 중요하므로 단위 테스트를 해야하는데, 그때문에 생성자를 공개하는 것은 비공개 메서드를 노출하지 않는 규칙을 위반하게 된다.  

Inquiry 생성자는 비공개이면서 식별할 수 있는 동작인 메서드의 예다. 따라서 이러한 경우에 생성자를 공개한다고 해서 테스트가 쉽게 깨지지는 않는다.  

### 비공개 상태 노출
또 다른 일반적인 안티 패턴으로 단위 테스트 목적으로만 비공개 상태를 노출하는 것이 있다.  
테스트는 제품 코드와 정확히 같은 방식으로 테스트 대상 시스템과 상호 작용해야 하며, 특별한 권한이 따로 있어서는 안된다.  

```C#
public class Customer {
    private CustomerStatus _status = CustomerStatus.Regular;
    
    public void Promote() {
        _status = CustomerStatus.Preferred;
    }
    
    public decimal GetDiscount() {
        return _status == CustomerStatus.Preferred ? 0.05m : 0;
    }
    
}
public enum CustomerStatus {
    Regular,
    Preferred
} 
```

이런 코드가 있을때 Promote() 메서드를 어떻게 테스트 해야할까? 메서드 내부에서 필드를 변경하는데, 해당 필드는 비공개이므로 테스트할 수 없다.  
이 필드를 공개하여 테스트 하고 싶다는 생각이 들것이다.  
하지만 이는 안티패턴이다. _status 필드는 비공개이므로 식별할 수 있는 동작이 아니다. 해당 필드를 공개하면 테스트가 구현 세부 사항에 결합된다.  
그렇다면 어떻게 테스트 해야할까?  

그 방법은 제품 코드가 이 클래스를 어떻게 사용하는지를 살펴보는 것이다.  
만약 제품 코드가 고객의 상태 (status) 를 신경쓴다면 해당 필드가 공개되어야 하는 것이고 (테스트 관점이 아닌 제품 코드 관점에서) 아니라면 공개되면 안된다.  
현재 제품 코드가 이 클래스에 대한 관심사는 할인 정보이기 떄문에 고객 상태에 따른 할인 정보에 대해서 테스트를 한다.  
만약 이후 제품 코드가 고객 상태 필드를 사용하게 된다면, 그때는 테스트에서 해당 필드를 테스트 할수 있을 것이다.  

### 테스트로 유출된 도메인 지식 
도메인 지식을 테스트로 유출하는 것은 또 하나의 흔한 안티 패턴이다.  
다음은 잘못된 테스트 방법을 보여준다.

```c#
public static class Caculator {
    public static int Add(int value1, int value2) {
        return value1 + value2;
    }
}

public class CaculatorTests {
    [Fact]
    public void Adding_two_numbers() {
        int value1 = 1;
        int value2 = 3;
        int expected = value1 + value2;
        
        int actual = Caculator.Add(value1, value2);
        
        Assert.Equal(expected, actual);
    }
    
    // 매개변수화 버전
    [Theory]
    [InlineData(1,3)]
    [InlineData(11,33)]
    public void Adding_two_numbers(int value1, int value2) {
        int expected = value1 + value2;
        
        int actual = Caculator.Add(value1, value2);
        
        Assert.Equal(expected, actual);
    }
}
```

위 예제는 처음에는 괜찮아 보이지만 사실은 안티 패턴이다.  
제품 코드에서 알고리즘 구현 부분을 복사했는데, 구현 세부 사항과 결합 되는 또 다른 예이다.  
이런 테스트는 리팩터링 내성 지표에서 거의 0점을 받게 되고 타당한 실패와 거짓 양성을 구별할 가능성이 없다.  
알고리즘 변경으로 인해 테스트가 실패하면 개발 팀은 원인을 파악하려고 노력하지 않으며 새 버전의 알고리즘을 테스트에 복사할 가능성이 높다.  

테스트를 작성할 때 특정 구현을 암시하지 말라.

```c#
public class CaculatorTests {
    // 매개변수화 버전
    [Theory]
    [InlineData(1,3,4)]
    [InlineData(11,33,44)]
    public void Adding_two_numbers(int value1, int value2, int expected) {
        int actual = Caculator.Add(value1, value2);
        
        Assert.Equal(expected, actual);
    }
}
```

처음에는 직관적이지 않아 보일 수 있지만, 단위 테스트는 예상 결과를 하드 코딩 하는 것이 좋다.


### 코드 오염
코드 오염은 테스트에만 필요한 제품 코드를 추가 하는 것이다.  
코드 오염은 다양한 유형의 스위치 형태를 취한다

```c# 
public class Logger {
    private readonly bool _isTestEnvironment;
    
    public Logger(bool isTestEnvironment) {
        _isTestEnvironment = isTestEnvironment;
    }
    
    public void Log(string text) {
        if (_isTestEnvironment)
            return;
            
        // log...
    }
}
```
위 예제는 클래스가 운영환경에서 실행되는지, 테스트 환경에서 실행 되는지 여부를 나타내는 변수가 있다.  
이런 코드의 문제는 테스트 코드와 제품 코드가 혼재되어 유지비가 증가하는 것이다. 테스트 코드를 제품 코드와 분리해야 한다.  
위와 같은 경우 상위 인터페이스를 도입하여 운영을 위한 구현제, 테스트를 목적으로 하는 가짜 구현체를 만들어라.  

### 구체 클래스를 목으로 처리하기 
구체 클래스를 목으로 처리해서 때때로 유용할 수 있지만, 이는 단일 책임 원칙을 위배하는 중대한 단점이 있다. 
프로세스 외부 의존성을 호출하는 클래스가 있다고 하자.
```
public class StatisticsCalculator {

    public (double weight, double totalCost) Calculate(int customerId) {
        List<DeliveryRecord> records = GetDeliveries(customerId);
        double totalWeight = records.Sum(x => x.Weight);
        double totalCost = records.Sum(x => x.Cost);
        
        return (totalWeight, totalCost);
    }
    
    public List<DeliveryRecord> GetDeliveries(int customerId) {
        // 프로세스 외부 의존성을 호출
    }
} 

public class CustomerController {
    private readonly StatisticsCalculator _calculator;
    
    public CustomerController(StatisticsCalculator calculator) {
        _calculator = calculator;
    }
    
    public string GetStatistics(int customerId) {
        (double totalWeight, double totalCost) = _calculator.Calculate(customerId);
        
        return 
            $"total weight delivered : {totalWeight}. " +
            $"total cost : {totalCost}";
    }
}
```

위의 예제에서 컨트롤러를 어떻게 테스트 해야할까?  
실제 StatisticsCalculator 인스턴스는 비관리 프로세스 외부 의존성을 참조하기 때문에 직접 넣을수는 없다.  
비관리 의존성을 스텁으로 대체하면서 StatisticsCalculator 를 완전히 교체하고 싶지는 않다.  
해결 방법중 하나는 StatisticsCalculator 를 목으로 처리하고 GetDeliveries() 메서드를 재정의 하는 것이다.  
하지만 이는 안티 패턴이다. 

> 일부 기능을 지키려고 구체 클래스를 목으로 처리해야 한다면 단일 책임 원칙을 위반하는 결과다.

StatisticsCalculator 는 비관리 의존성과 통신하는 책임, 통계를 계산하는 책임이 서로 관련이 없음에도 결합되어 있다.  
이 두 책임을 별도 클래스로 분할한다.

```
public class DeliveryGateway : IDeliveryGateway {
    public List<DeliveryRecord> GetDeliveries(int customerId) {
        // 외부 의존성 호출
    }
}

public class StatisticsCalculator {

    public (double weight, double totalCost) Calculate(List<DeliveryRecord> records) {
        double totalWeight = records.Sum(x => x.Weight);
        double totalCost = records.Sum(x => x.Cost);
        
        return (totalWeight, totalCost);
    }
    
}

public class CustomerController {
    private readonly StatisticsCalculator _calculator;
    private readonly IDeliveryGateway _gateway;
    
    public CustomerController(StatisticsCalculator calculator, IDeliveryGateway gateway) {
        _calculator = calculator;
        _gateway = gateway;
    }
    
    public string GetStatistics(int customerId) {
    
        var records = _gateway.GetDeliveries(customerId);
        (double totalWeight, double totalCost) = _calculator.Calculate(records);
        
        return 
            $"total weight delivered : {totalWeight}. " +
            $"total cost : {totalCost}";
    }
}
``` 

비관리 의존성과 통신하는 책임을 DeliveryGateway 에 넘겼고, 이는 상위 인터페이스가 있으므로 테스트 코드에서 구체 클래스 대신 인터페이스를 목으로 사용할 수 있다.  
이는 위에서 살펴봤던 험블 객체 디자인 패턴의 실제 예이다. 