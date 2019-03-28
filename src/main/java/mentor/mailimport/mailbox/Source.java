package mentor.mailimport.mailbox;

import javax.mail.Message;
import java.io.IOException;
import javax.mail.MessagingException;

public interface Source {

    void init() throws IOException, MessagingException;

    void setMessages(Message[] messages) throws MailBoxSourceException;

    Message[] getLastMessages() throws MailBoxSourceException, MessagingException;

}
