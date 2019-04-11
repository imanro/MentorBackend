package mentor.importer.mailbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.omg.SendingContext.RunTime;
// import org.apache.tomcat.util.json.JSONParser;
// import org.apache.tomcat.util.json.JSONParser;

import javax.mail.Message;
import javax.mail.*;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.net.URL;
import java.io.File;
import java.io.FileReader;
// import org.json.simple.*;
// import org.json.simple;
// import com.fasterxml.jackson.annotation.JacksonAnnotation;


public class SourceMyImap implements Source {

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
            Message[] messages = folder.getMessages(totalCount - 50, totalCount);
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

        String[] creds = getCredentials();

        Session session = Session.getDefaultInstance(props, null);
        // session.setDebug(true);
        Store store = session.getStore("imaps");
        store.connect(creds[0], creds[1], creds[2]);
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

    private String[] getCredentials() throws RuntimeException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("mail-creds.json");

        if (resource == null) {
            throw new RuntimeException("cred file is not found!");
        }

        File file = new File(resource.getFile());
        String contents;
        try {
             contents = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException("Could not read from file");
        }


        // I could not install org.json :(((
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(contents);

        JsonNode node;
        try {
            node = mapper.readTree(contents);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing json");
        }

        String[] result = new String[3];
        result[0] = node.get("host").textValue();
        result[1] = node.get("user").textValue();
        result[2] = node.get("password").textValue();

        return result;
    }
}
