package mentor.importer.mailbox;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

public interface Source {

    void init() throws IOException, MessagingException;

    void setMessages(Message[] messages) throws MailBoxSourceException;

    Message[] getLastMessages() throws MailBoxSourceException, MessagingException;

}
