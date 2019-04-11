package mentor.importer;

import mentor.expression.Expression;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.StreamUtils;

class MessageParserTest {

    private MessageParser parser = new MessageParser();

    @Test
    void attachmentExtracted() throws IOException {
        String contents = this.getFileContent("mail.txt");
        contents = parser.extractAttachment(contents);
        assertThat(contents.indexOf("<html>"), is(0));
        //        System.out.println("This test method should be run1");
    }

    @Test
    void decoded() throws MessagingException, IOException {
        String contents = this.getFileContent("attachment_encoded.txt");
        String result = parser.decode(contents);
        assertThat(result, containsString("<html>"));
        assertThat(result, not(containsString("=D0")));
    }

    @Test
    void linesExtracted() throws IOException, MessageParserException {

        String contents = this.getFileContent("attachment_decoded.html");

        List<String> lines = parser.extractLines(contents);

        assertThat(lines.size(), is(7));
    }

    @Test
    void expressionListCreated() {

        List<String> lines = this.getContentLines();
        List<Expression> expressions = parser.parseLines(lines);
        assertThat(expressions.size(), is(2));
    }

    @Test
    void expressionsCreatedValid() throws RuntimeException {

        List<String> lines = this.getContentLines();
        List<Expression> expressions = parser.parseLines(lines);

        if (expressions.size() > 0) {
            Expression expression = expressions.get(0);
            assertThat(expression.getTerm(),notNullValue());
            assertThat(expression.getExample(), notNullValue());
            // System.out.println(mentor.expression.example);
            // System.out.println(mentor.expression.translation);
            assertThat(expression.getTranslation(), notNullValue());
        } else {
            throw new RuntimeException("The created list is empty");
        }
    }

    @Test
    void highlightTagsCreated() {
        String input = this.getExampleString();
        String result = parser.convertHighlights(input);
        assertThat(result, containsString("<strong>"));
    }

    @Test
    void textNormalized() {
        String input = this.getExampleString();
        String result = parser.normalize(input);
        assertThat(result, not(containsString("<span>")));
        assertThat(result, not(containsString("<hr>")));
        assertThat(result, not(containsString("⤷")));
        assertThat(result, not(containsString("\n")));
        assertThat(result, not(containsString("| |")));
        assertThat(result.substring(result.length() - 1), not(" "));
        assertThat(result.substring(0, 1), not(" "));
        assertThat(result, not(containsString("<td>")));
    }

    @Test
    void newlinesCleared() {
        String input = this.getExampleString();
        String result = parser.convertHighlights(input);
        result = parser.normalize(result);
        assertThat(result, not(containsString("\n")));
    }


    @Test
    void highlightsLeft() {
        String input = this.getExampleString();
        String result = parser.convertHighlights(input);
        result = parser.normalize(result);
        assertThat(result, containsString("<strong>"));
    }

    @Test
    void languagesParsed() {
        String langSpec = "   Russian &gt; English    ";
        String[] result = parser.parseLanguages(langSpec);
        assertThat(result.length, is(2));
        assertThat(result[0], is("russian"));
        assertThat(result[1], is("english"));
    }

    @Test
    void languagesSet() {
        List<String> lines = this.getContentLines();
        List<Expression> expressions = parser.parseLines(lines);
        assertThat(expressions.size(), is(2));
        assertThat(expressions.get(0).getSrcLang(), is("english"));
        assertThat(expressions.get(0).getTrgLang(), is("russian"));
        assertThat(expressions.get(1).getSrcLang(), is("french"));
        assertThat(expressions.get(1).getTrgLang(), is("russian"));
    }

    private List<String> getContentLines() {
        List<String> lines = new ArrayList<>();
        lines.add("<td bgcolor=\"#f2f2f2\"> English &gt; Russian </td>");
        lines.add("<td>compelled</td>");
        lines.add("<td><span " +
                "style=\"font-family:  Calibri; font-size: 14.000000; color:" +
                "#2B6FAD; text-align: justify;\">All cultures, civilizations and\n" +
                "faiths are now <span style=\"background-color:#FAFADC; color:" +
                "#0C0054;\">compelled</span> to inhabit the same world by the" +
                "inviolable verdict of technology.</span> <br>⤷  <span " +
                "style=\"font-family:  Calibri; font-size: 13; color: #0C0054;" +
                "text-align: justify;\">Все культуры, цивилизации и религии" +
                "<b><span style=\"background-color:#FAFADC;\">вынуждены</span></b> " +
                "в силу нерушимого вердикта технологии жить теперь в едином" +
                "мире.</span><hr> |  | </td>");

        lines.add("<tr><td bgcolor=\"#f2f2f2\"> French &gt; Russian </td>");
        lines.add("<td>accuser</td>");
        lines.add("<td><span style=\"font-family:  Calibri; " +
                "font-size: 14.000000; color: #2B6FAD; text-align: justify;\">De ce dont vous venez nous " +
                "<span style=\"background-color:#FAFADC; color: #0C0054;\">accuser</span>.</span> <br>⤷  " +
                "<span style=\"font-family:  Calibri; font-size: 13; color: #0C0054; text-align: justify;\">За всё, в чём вы пришли <b><span style=\"background-color:#FAFADC;\">обвинять</span></b> нас.</span></td>");

        return lines;
    }

    private String getExampleString() {
        return "<span " +
                "style=\"font-family:  Calibri; font-size: 14.000000; color:" +
                "#2B6FAD; text-align: justify;\">All cultures, civilizations and\n" +
                "faiths are now <span style=\"background-color:#FAFADC; color:" +
                "#0C0054;\">compelled</span> to inhabit the same world by the" +
                "inviolable verdict of technology.</span> <br>⤷  <span " +
                "style=\"font-family:  Calibri; font-size: 13; color: #0C0054;" +
                "text-align: justify;\">Все культуры, цивилизации и религии" +
                "<b><span style=\"background-color:#FAFADC;\">вынуждены</span></b> " +
                " в силу нерушимого вердикта технологии жить теперь в едином" +
                "мире.</span>";
    }

    private String getFileContent(String fileName) throws IOException {
        InputStream inputStream =
                new DefaultResourceLoader().getResource("classpath:/" + fileName).getInputStream();
        String contents = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        inputStream.close();
        return contents;
    }
}
