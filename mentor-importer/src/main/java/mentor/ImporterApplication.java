package mentor;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;


import mentor.expression.Expression;
import mentor.expression.ExpressionRepository;

import mentor.importer.MessageParser;
import mentor.importer.mailbox.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.ComponentScan;

// The next functionality should be not empty repository but add non existing expressions there

// @ComponentScan("mentor.expression")
@SpringBootApplication(scanBasePackages = "mentor")
// @SpringBootApplication
public class ImporterApplication implements CommandLineRunner {

    @Autowired
    private ExpressionRepository repository;

    @Autowired
    private MessageParser parser;

    public static void main(String[] args) {
        SpringApplication.run(ImporterApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // ExpressionRepository repository = new ExpressionRepository();
        // create parser,

        String needleSubject = "Your Reverso Phrasebook export";

        System.out.println("Hello, I'm Mentor importer!");
        MailBox mailBox = getMailBox();

        String messageContents;

        try {
            messageContents = importLastMessage(mailBox, needleSubject);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
            // System.out.println("There is no such messages :(");
            // System.exit(0);
        }

        if (messageContents != null && messageContents.length() > 0) {

            saveMessage(messageContents);

            List<String> lines = parser.extractLines(messageContents);
            System.out.println("We have found " + lines.size() + " of lines");
            List<Expression> expressions = parser.parseLines(lines);
            System.out.println("We have found " + expressions.size() + " of expressions");


            if (lines.size() > 0) {
                // save expressions in database
                System.out.println("Truncating repository (need to be changed)");
                repository.deleteAll();

                for (int i = 0; i < expressions.size(); i++) {
                    repository.save(expressions.get(i));
                }
            } else {
                if (expressions.size() == 0) {
                    System.out.println("Perhaps you have export your Phrasebook in condensed format");
                }
            }

        } else {
            System.out.println("Obviously, the import has been sunk in the mailbox");
        }
    }

    private static String importLastMessage(MailBox mailBox, String needleSubject) {
        Message[] messages;

        try {
            messages = mailBox.getLastMessages();
        } catch (MailBoxSourceException | MailBoxException | MessagingException e) {
            throw new RuntimeException(e);
        }

        Message[] selected;

        try {
            selected = mailBox.selectMessagesBySubject(messages, needleSubject);
        } catch (RuntimeException e) {
            System.out.println("ex");
            throw new RuntimeException(e);
        }

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

    private static void saveMessage(String contents) {
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
