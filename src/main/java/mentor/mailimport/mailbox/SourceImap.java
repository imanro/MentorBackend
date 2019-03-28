package mentor.mailimport.mailbox;

import org.apache.commons.lang3.ArrayUtils;

import javax.mail.Message;
import javax.mail.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class SourceImap implements Source {

    private Folder folder;

    public void init() throws MessagingException {
        Store store = this.getConnectedStore();
        Folder folder = this.getStoreFolder(store);
        this.setFolder(folder);
    }

    @Override
    public Message[] getLastMessages() throws MailBoxSourceException, MessagingException {
        if (this.folder != null) {
            // Message[] messages = folder.search(new FlagTerm(new Flags(Flag.RECENT), false));

            int totalCount = folder.getMessageCount();
            Message[] messages = folder.getMessages(totalCount - 20, totalCount);
            ArrayUtils.reverse(messages);

            /*
            for (int i = 0; i < messages.length; i++) {
                System.out.println(messages[i].getSubject());
                System.out.println(messages[i].getReceivedDate());
            }
            */

            return messages;
        } else {
            throw new MailBoxSourceException();
        }
    }

    public void setMessages(Message[] messages) throws MailBoxSourceException {
        throw new MailBoxSourceException("This method is not usable here");
    }

    private Store getConnectedStore() throws  MessagingException {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        Session session = Session.getDefaultInstance(props, null);
        // session.setDebug(true);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com","roman.denisov@gmail.com", "wm91rn2maC");
        return store;
    }

    private Folder getStoreFolder(Store store)  throws  MessagingException {
        Folder folder = store.getFolder("Inbox");
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    private void setFolder(Folder folder) {
        this.folder = folder;
    }
}
