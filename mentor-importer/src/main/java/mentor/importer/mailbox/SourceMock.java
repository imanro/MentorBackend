package mentor.importer.mailbox;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class SourceMock implements Source {

    private Message [] messages;

    @Override
    public void init() {
    }

    @Override
    public Message[] getLastMessages() {
        return this.messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }

    public static Message createMockMessage(String subject, Date date)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        Message message = new MimeMessage(session);

        message.setSubject(subject);
        message.setSentDate(date);

        return message;
    }
}
