### 커맨드 패턴

실행될 기능을 캡슐화 함으로써 요청을 하는 객체와 요청을 수행하는 객체를 분리한다.  
이를 위해 커맨드 패턴을 활용한다.

![img](https://user-images.githubusercontent.com/37106689/70050448-4faa3a80-1612-11ea-902e-20268fbf3bbf.png)

```
Invoker : 커맨드 객체를 가지고 있으며, execute() 를 실행해 커맨드 객체에게 특정 작업을 할것을 명령한다.
Command : 모든 커맨드 객체에서 구현해야 하는 인터페이스. 모든 명령은 execute() 를 통해 수행된다.
ConcreteCommand : 리시버를 갖고 있고, 특정 행동과 리시버를 연결한다.
Receiver : 실제 어떤 일을 수행하는지 알고 있는 객체이다.
```

간단한 예제를 통해 커맨드 패턴을 구현해보자

#### 작업 큐 시스템

어떤 작업이 끝나면 SMS, 메일, 채팅으로 알려줘야 하는 시스템이 있다.  
각 작업은 작업큐로 던지고, 작업큐를 주기적으로 폴링하며 작업들을 처리한다.  
작업을 처리하는 녀석은 작업이 무엇인지 신경쓰지 않고, 단지 실행시키기만 하면 된다.

실제 어떤 일을 수행할지 알고 있는 객체들이다.
```java
public class Mail {
    public void action() {
        System.out.println("send mail");
    }
}

class SMS {
    public void action() {
        System.out.println("send SMS");
    }
}

class Chat {
    public void action() {
        System.out.println("send Chat");
    }
}
```

실제 작업을 수행하는 객체들을 갖고 있는 커맨드 객체 들이다.  
작업큐에서 작업을 실행시키는 녀석은 단지 이 객체들을 꺼내서 execute 만 실행시키면 된다.
```java
package command;

public interface Command {
    void execute();
}

class MailSender implements Command{
    private Mail mail;

    public MailSender(Mail mail) {
        this.mail = mail;
    }

    @Override
    public void execute() {
        mail.action();
    }
}

class SMSSender implements Command {
    private SMS sms;

    public SMSSender(SMS sms) {
        this.sms = sms;
    }

    @Override
    public void execute() {
        sms.action();
    }
}

class ChatSender implements Command {
    private Chat chat;

    public ChatSender(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void execute() {
        chat.action();
    }
}

public class JobExecutor {

    private Command command;

    public JobExecutor(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void run() {
        command.execute();
    }
}
```

각 익스큐터들은 처리할 작업을 작업큐에 담고, 작업큐를 처리하는 객체는 작업큐에서 작업을 꺼내 처리한다.
여러 잡 익스큐터들을 시뮬레이션 하기 위해 쓰레드를 썼는데, 작업큐를 처리할때 간혹 NPE 가 난다.. 쓰레드는 역시 어렵다.
```java
public class CommandPattern {
    static Queue<Command> jobQueue = new LinkedList<>();
    
        public synchronized static Queue<Command> getQueue() {
            return jobQueue;
        }
    
        public static void main(String[] args) throws InterruptedException {
            Command mailCommand = new MailSender(new Mail());
            Command smsCommand = new SMSSender(new SMS());
            Command chatCommand = new ChatSender(new Chat());
    
            JobExecutor mailJobExecutor = new JobExecutor(mailCommand);
            JobExecutor smsJobExecutor = new JobExecutor(smsCommand);
            JobExecutor chatJobExecutor = new JobExecutor(chatCommand);
    
    
            ExecutorService es = Executors.newFixedThreadPool(10);
            es.execute(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(2);
                        getQueue().add(mailJobExecutor.getCommand());
                    } catch (InterruptedException e) {
                    }
                }
            });
            es.execute(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(2);
                        getQueue().add(smsJobExecutor.getCommand());
                    } catch (InterruptedException e) {
                    }
                }
            });
            es.execute(() -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(2);
                        getQueue().add(chatJobExecutor.getCommand());
                    } catch (InterruptedException e) {
                    }
                }
            });
    
            es.awaitTermination(2, TimeUnit.SECONDS);
            es.shutdown();
    
            while(!jobQueue.isEmpty()) {
                Command command = jobQueue.poll();
                command.execute();
            }
        }
}
```