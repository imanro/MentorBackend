package mentor.mailimport.mailbox;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class MailBoxTest {

    private MailBox mailBox;
    private String needleSubject = "Your Reverso Phrasebook export";
    private String randomSubject1 = "Welcome to our beach";
    private String randomSubject2 = "Extend your pocket today";

    @Test
    void messagesListReceived() throws MailBoxSourceException, MailBoxException, MessagingException {
        this.initMailBox();
        Source source = this.mailBox.getSource();
        Message[] testMessages = createTestMessages();
        source.setMessages(testMessages);
        Message[] messages = mailBox.getLastMessages();
        assertThat(messages.length, greaterThan(0));
    }

    @Test
    void needleMessagesSelected() {
        this.initMailBox();
        Message[] testMessages = this.createTestMessages();
        Message[] selectedMessages = mailBox.selectMessagesBySubject(testMessages, this.needleSubject);

        assertThat(selectedMessages.length, greaterThan(0));
        assertThat(selectedMessages.length, lessThan(testMessages.length));
    }

    @Test
    void lastNeedleMessageChoosen() {
        this.initMailBox();
        Message[] testMessages = this.createTestMessages();
        Message latest = mailBox.getLatestMessage(testMessages);

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        try {
            assertThat(format.format(latest.getSentDate()), equalTo("2018/03/09"));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void isMessageContentsReturned() {
        this.initMailBox();

        String htmlContent = "<html><body>I am message</body></html>";
        Message htmlMessage = this.createTestMessageWithHtmlContents(htmlContent);

        String textContent = "I am message";
        Message textMessage = this.createTestMessageWithTextContents(textContent);

        String textResult = mailBox.getMessageContentsAsString(textMessage);
        String htmlResult = mailBox.getMessageContentsAsString(htmlMessage);

        assertEquals(textContent, textResult, "This strings should be equal");
        assertEquals(htmlContent, htmlResult, "This strings should be equal");
    }

    /*
    @Test
    void testRealRun() throws MailBoxSourceException, MailBoxException, MessagingException {
        this.initRealMailBox();
        Message[] messages = mailBox.getLastMessages();


        Message[] selected = mailBox.selectMessagesBySubject(messages, needleSubject);
        Message needle = mailBox.getLatestMessage(selected);

        if (needle != null) {
            String contents = mailBox.getMessageContentsAsString(needle);
            System.out.println(contents);
        }

        // System.out.println(needle);
    }
    */

    private void initMailBox() {
        mailBox = new MailBox();
        Source source = new SourceMock();
        mailBox.setSource(source);

        try {
            mailBox.getSource().init();
        } catch (MessagingException | IOException | MailBoxException e) {
            throw new RuntimeException(e);
        }
    }

    private void initRealMailBox() {
        mailBox = new MailBox();
        Source source = new SourceImap();
        mailBox.setSource(source);
        try {
            mailBox.getSource().init();
        } catch (MessagingException | IOException | MailBoxException e) {
            throw new RuntimeException(e);
        }
    }

    private Message[] createTestMessages() {

        Message[] messages = new Message[5];

        try {
            messages[0] = SourceMock.createMockMessage(this.randomSubject1, this.createDate("2018/03/09 23:01:00"));
            messages[1] = SourceMock.createMockMessage(this.needleSubject, this.createDate("2018/03/06 23:01:00"));
            messages[2] = SourceMock.createMockMessage(this.needleSubject, this.createDate("2018/03/08 23:01:00"));
            messages[3] = SourceMock.createMockMessage(this.needleSubject, this.createDate("2018/03/07 23:01:00"));
            messages[4] = SourceMock.createMockMessage(this.randomSubject1, this.createDate("2018/03/08 23:01:00"));

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return messages;
    }

    private Message createTestMessageWithHtmlContents(String htmlContents) {
        Message message;

        try {
            message = SourceMock.createMockMessage(this.needleSubject, this.createDate("2018/03/09 23:01:00"));

            Multipart multi = new MimeMultipart();
            BodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(htmlContents, "text/html; charset=utf-8");
            multi.addBodyPart(textBodyPart);
            message.setContent(multi);
            message.saveChanges(); // this is required

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    private Message createTestMessageWithTextContents(String textContents) {
        Message message;

        try {
            message = SourceMock.createMockMessage(this.needleSubject, this.createDate("2018/03/09 23:01:00"));
            message.setText(textContents);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    private Date createDate(String value) {
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


}
