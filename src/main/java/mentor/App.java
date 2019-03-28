package mentor;

import mentor.mailimport.MessageParser;
import mentor.mailimport.mailbox.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public class App {

    public static void main(String[] args) {
        // create parser,

        String needleSubject = "Your Reverso Phrasebook export";

        System.out.println("Hello, I'm Mentor importer!");
        MailBox mailBox = getMailBox();

        String messageContents = "";

        try {
            messageContents = importLastMessage(mailBox, needleSubject);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
            // System.out.println("There is no such messages :(");
            // System.exit(0);
        }

        if (messageContents.length() > 0) {
            MessageParser parser = getMessageParser();
            List<String> lines = parser.extractLines(messageContents);
            System.out.println("We have found " + lines.size() + " of lines");
            List<Expression> expressions = parser.parseLines(lines);
            System.out.println("We have found " + expressions.size() + " of expressions");

            // save expressions in database
            ExpressionRepository repository = getExpressionRepository();
            System.out.println("Truncating repository (need to be changed)");
            repository.drop();
            for(int i = 0; i < expressions.size(); i++) {
                repository.save(expressions.get(i));
            }

        } else {
            System.out.println("Obviously, there is no such message");
        }
    }

    private static String importLastMessage(MailBox mailBox, String needleSubject) {
        Message[] messages;

        try {
            messages = mailBox.getLastMessages();
        } catch (MailBoxSourceException | MailBoxException | MessagingException e ) {
            throw new RuntimeException(e);
        }

        Message[] selected;

        try {
            selected = mailBox.selectMessagesBySubject(messages, needleSubject);
        } catch (RuntimeException e) {
            System.out.println("ex");
            throw new RuntimeException(e);
        }

        System.out.println(selected.length);

        Message needle = mailBox.getLatestMessage(selected);

        if (needle != null) {
            String contents = mailBox.getMessageContentsAsString(needle);
            System.out.println(contents);
            return contents;

        } else {
            return null;
        }
    }

    private static MailBox getMailBox() {
        MailBox mailBox = new MailBox();
        Source source = new SourceImap();
        mailBox.setSource(source);
        try {
            mailBox.getSource().init();
        } catch (MessagingException | IOException | MailBoxException e) {
            throw new RuntimeException(e);
        }

        return mailBox;
    }

    private static MessageParser getMessageParser() {
        return new MessageParser();
    }

    private static ExpressionRepository getExpressionRepository() {
        ExpressionRepository repository = new ExpressionRepository();
        repository.initClient();
        repository.initDatabase("mentor");
        return repository;
    }
}
