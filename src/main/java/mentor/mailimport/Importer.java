package mentor.mailimport;

import mentor.Expression;
import mentor.ExpressionRepository;
import mentor.mailimport.mailbox.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

public class Importer {
    public boolean run() {

        String needleSubject = "Your Reverso Phrasebook export";

        System.out.println("Hello!");
        MailBox mailBox = getMailBox();
        String messageContents = importLastMessage(mailBox, needleSubject);

        if (messageContents != null) {

            saveMessage(messageContents);

            MessageParser parser = getMessageParser();
            List<String> lines = parser.extractLines(messageContents);
            System.out.println("We have found " + lines.size() + " of lines");
            List<Expression> expressions = parser.parseLines(lines);
            System.out.println("We have found " + expressions.size() + " of expressions");

            // save expressions in database
            ExpressionRepository repository = getExpressionRepository();
            System.out.println("Truncating repository (need to be changed)");
            repository.drop();

            for (int i = 0; i < expressions.size(); i++) {
                repository.save(expressions.get(i));
            }

            return true;

        } else {
            System.out.println("Obviously, there is no such message");
            return false;
        }
    }

    private static String importLastMessage(MailBox mailBox, String needleSubject) {
        Message[] messages;

        try {
            messages = mailBox.getLastMessages();
        } catch (MailBoxSourceException | MailBoxException | MessagingException e) {
            throw new RuntimeException(e);
        }

        Message[] selected = mailBox.selectMessagesBySubject(messages, needleSubject);

        Message needle = mailBox.getLatestMessage(selected);

        if (needle != null) {
            String contents = mailBox.getMessageContentsAsString(needle);
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

    private void saveMessage(String contents) {
        PrintStream out;

        System.out.println("attempt to save our message");

        try {
            out = new PrintStream("/tmp/message_contents.txt");
        } catch (FileNotFoundException e) {
            System.out.println("output in null");
            out = null;
        }

        if (out != null) {
            out.println(contents);
            out.close();
        }
    }
}
