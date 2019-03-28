package mentor.mailimport;

import mentor.Expression;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.IOException;
import javax.mail.MessagingException;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class MessageParser {

    public MessageParser() {
    }

    public String extractAttachment(String contents) {
        String header = "Content-Transfer-Encoding: quoted-printable";
        int position = contents.lastIndexOf(header);

        if (position != -1) {
            contents = contents.substring(position + header.length());
            contents = contents.replaceAll("--Apple-Mail.+", "");
            return contents.trim();

        } else {
            return contents;
        }
    }

    public String decode(String input) throws MessagingException, IOException {
        //	byte[] bytes = Base64.getDecoder().decode(input.trim());
        byte[] bytes = input.trim().getBytes();
        InputStream decodedStream = MimeUtility.decode(new ByteArrayInputStream(bytes), "quoted-printable");
        byte[] tmp = new byte[input.length()];
        int n = decodedStream.read(tmp);
        byte[] res = new byte[n];
        System.arraycopy(tmp, 0, res, 0, n);
        String decoded = new String(res);
        return decoded;
    }

    public List<String> extractLines(String input) throws MessageParserException {
        // extract the table body first
        String pattern = "<tbody>(.+)</tbody>";
        Pattern r = Pattern.compile(pattern, Pattern.DOTALL);

        Matcher m = r.matcher(input);
        String tbody;

        if (m.find()) {
            tbody = m.group(1);
        } else {
            throw new MessageParserException("tbody tag could not be found");
        }

        pattern = "<tr>(.+?)</tr>";

        r = Pattern.compile(pattern, Pattern.DOTALL);
        m = r.matcher(tbody);

        List<String> found = new ArrayList<String>();

        while (m.find()) {
            found.add(m.group(1));
        }

        return found;
    }

    public List<Expression> parseLines(List<String> lines) throws RuntimeException {
        List<Expression> expressions = new ArrayList<Expression>();

        String patternBr = "<br>";
        Pattern reBr = Pattern.compile(patternBr);
        Pattern reTerm = Pattern.compile("<td>(.+?)</td>");
        Expression exp = new Expression();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher mBr = reBr.matcher(line);

            if (mBr.find()) {
                if (exp.getTerm() != null) {
                    // continue to fill

                    String[] parts = line.split(patternBr);

                    if (parts.length == 2) {

                        exp.setExample(parts[0]);
                        exp.setTranslation(parts[1]);
                        exp.setExample(this.convertHighlights(exp.getExample()));
                        exp.setExample(this.normalize(exp.getExample()));
                        exp.setTranslation(this.convertHighlights(exp.getTranslation()));
                        exp.setTranslation(this.normalize(exp.getTranslation()));

                        // split by that br
                        expressions.add(exp);

                    }

                }

            } else {
                Matcher mTerm = reTerm.matcher(line);
                if (mTerm.find()) {
                    exp = new Expression();
                    exp.setTerm(mTerm.group(1));
                }
            }
        }

        return expressions;
    }

    public String convertHighlights(String string) {
        // just convert some <span .+> into <strong>
        Pattern reSpan = Pattern.compile("(<span style=\"background.+?>)(.+?)(</span>)");
        Matcher m = reSpan.matcher(string);
        if (m.find()) {
            string = string.replaceFirst(m.group(1), "<strong>");
            string = string.replaceFirst(m.group(3), "</strong>");
        }

        return string;
    }

    public String normalize(String string) {
        Whitelist whitelist = Whitelist.none();
        whitelist.addTags("strong");
        String result = Jsoup.clean(string, whitelist);
        result = result.replaceAll("\n", "");
        result = result.replaceAll("⤷", "");
        result = result.replaceAll("\\|\\s+\\|", "");
        return result.trim();
    }

}
