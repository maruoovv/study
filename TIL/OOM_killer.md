## OOM Killer

서버에서 구동되던 java 앱이 갑자기 죽는 경우가 발생했다.  
-XX:-HeapDumpOnOutOfMemoryError -XX:HeapDumpPath= 를 설정해놨기에, OOM 이 나서 죽은거라면 로그가 남아있어야 할텐데 남아 있지 않았다.  

OOM Killer 라는 시스템이 있는데 서버 메모리가 부족할 경우, 특정 프로세스를 강제로 종료 시킨다.
```
실제로는, 커널은 VM을 이용해 프로세스에 메모리를 할당하기 때문에 실제 물리메모리 보다 큰 프로그램을 구동시킬 수 있다.
당장 사용하지 않는 메모리는 나중에 할당하기 때문에, 실제 메모리를 넘는 프로그램도 구동되는 것이다(OverCommit)
이 때, OverCommit 된 메모리가 쓰여지게 되는 경우, 메모리가 모자라 OOM 이 발생한다.  
출처 : https://mozi.tistory.com/28
```
이렇게 종료되는 프로세스는 프로세스의 로그에 남지 않고, /var/log/messages 에 남게 된다.  

### linux os 로그 보기
/var/log/messages 를 보면 linux os 가 남긴 로그를 확인할 수 있다.  
```
Apr 1 13:00:00 server-name kernel: Out of memory: Kill process 0000
```
