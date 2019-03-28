package mentor.mailimport.mailbox;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.lang.RuntimeException;

// import Arrays;

public class MailBox {

    private Source source;

    public Message[] getLastMessages() throws MailBoxException, MailBoxSourceException, MessagingException {
        return this.getSource().getLastMessages();
    }

    public Message[] selectMessagesBySubject(Message[] messages, String subject) throws RuntimeException {

        Stream<Message> s = Arrays.stream(messages).filter(m -> {
            try {
                // System.out.println(m.getSubject());
                // System.out.println(m.getSubject().contains(subject));

                return m.getSubject().contains(subject);
            } catch (MessagingException | NullPointerException e) {
                return false;
                // throw new RuntimeException(e);
            }
        });

        return s.toArray(Message[]::new);
    }

    public Message getLatestMessage(Message[] messages) throws NoSuchElementException {

        Comparator<Message> comparator = Comparator.comparing(
                m -> {
                    try {
                        return m.getSentDate();
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        return Arrays.stream(messages).filter(m -> {
            try {
                return m.getSentDate() != null;
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        })
                .max(comparator)
                .orElse(null);
    }

    public String getMessageContentsAsString(Message message) {
        String result = "";

        try {
            if (message.isMimeType("text/plain")) {
                try {
                    result = message.getContent().toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else if (message.isMimeType("multipart/*")) {
                try {
                    MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                    result = getFirstHtmlMimeMultipart(mimeMultipart);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {

        List<String> rows = new ArrayList<String>();

        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/plain")) {

                rows.add((String) bodyPart.getContent());

            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                rows.add((String) html);

            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                rows.add(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }

        return String.join("\n", rows);
    }

    private String getFirstHtmlMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {

        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/html")) {
                return (String) bodyPart.getContent();
            }
        }

        return "";
    }


    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource()
            throws MailBoxException {
        if (this.source != null) {
            return this.source;
        } else {
            throw new MailBoxException("The mailbox is not assigned yet");
        }

    }

}
