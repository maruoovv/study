package command;

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
