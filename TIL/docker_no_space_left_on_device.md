docker 이미지를 이용한 배포 중 다음과 같은 에러가 발생했다.

**write /docker/lib/ojdbc7.jar: no space left on device**

서버의 디스크 용량은 충분한데, 용량이 없다는 에러가 발생했다.  

이 문제는 os가 도커를 위한 Virtual Memory 영역을 만들어서 사용하는데,  
이 영역을 모두 사용하여 이미지를 풀/빌드 할수 없어 발생 하는 문제이다.  

로컬에 저장된 사용하지 않는 이미지들을 정리 해주거나  
> docker image prune -a

도커에 할당된 공간을 늘리는 방법이 있다. 