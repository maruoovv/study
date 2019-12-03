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
