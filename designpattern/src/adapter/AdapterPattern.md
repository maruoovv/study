### 어댑터 패턴 

한 클래스의 인터페이스를 클라이언트에서 사용하고자 하는 다른 인터페이스로 변환한다.  
인터페이스 호환성 문제로 같이 쓸수 없는 클래스들을 연결해서 쓸 수 있다.  
이렇게 함으로써 클라이언트와 구현을 분리할 수 있으며, 추후 인터페이스가 바뀌더라도 어댑터에 캡슐화 되기 때문에,
클라이언트는 바뀌지 않아도 된다.

![img](https://user-images.githubusercontent.com/37106689/70136692-d83ae080-16cf-11ea-8a66-1c833c871cf9.png)

```
Client : 클라이언트는 타겟 인터페이스만 호출한다.
Adapter: 어댑터는 타켓 인터페이스를 구현하고, 어댑티 인스턴스를 갖고 있다. 요청이 들어올 시 어댑티를 호출한다.
Adaptee: 모든 요청은 어댑티에게 위임되어 처리한다.
```

간단한 예제를 통해 어댑터 패턴을 구현해보자.  

---

카메라는 사진을 찍는 기능을 한다.   
초기에는 카메라의 버튼을 눌러야 사진이 찍혔다.
```java
public class Camera {
    public void capture() {
        System.out.println("captured by camera");
    }
}

public class AdapterPattern {
    public static void main(String[] args) {
        Camera camera = new Camera();
       
        camera.capture();
    }
}
```

추후에 기능이 추가되어, 카메라의 버튼이 아닌 리모콘을 눌러 사진을 찍을 수 있게 되었다.  
하지만 Camera 클래스는 변경이 불가능한 상황이다.
이럴때 어댑터 패턴을 이용하여 해결할 수 있다.
 
```java
public interface CameraAdapter {
    void capture();
}

class RemoteCapture implements CameraAdapter {
    private Camera camera;

    public RemoteCapture(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void capture() {
        System.out.println("remoteCapture button pressed");
        camera.capture();
    }
}

public class AdapterPattern {
    public static void main(String[] args) {
        Camera camera = new Camera();
        CameraAdapter captureAdapter = new RemoteCapture(camera);

        camera.capture();
        captureAdapter.capture();
    }
}
```