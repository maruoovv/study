package command;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandPattern {
    private static Queue<Command> jobQueue = new ConcurrentLinkedQueue<>();

    private static Queue<Command> getQueue() {
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
                getQueue().add(mailJobExecutor.getCommand());
            }
        });
        es.execute(() -> {
            for (int i = 0; i < 10; i++) {
                getQueue().add(smsJobExecutor.getCommand());
            }
        });
        es.execute(() -> {
            for (int i = 0; i < 10; i++) {
                getQueue().add(chatJobExecutor.getCommand());
            }
        });

        es.awaitTermination(2, TimeUnit.SECONDS);
        es.shutdown();

        while (!jobQueue.isEmpty()) {
            Command command = jobQueue.poll();
            command.execute();
        }
    }
}
