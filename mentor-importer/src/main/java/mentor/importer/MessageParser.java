package mentor.importer;

import mentor.expression.Expression;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public List<Expression> parseLines(List<String> lines) throws RuntimeException, MessageParserException {
        List<Expression> expressions = new ArrayList<Expression>();

        String patternExample = "<br>";
        String patternLang = "<td bgcolor=\"#f2f2f2\">(.+?)</td>";

        Pattern reExample = Pattern.compile(patternExample);
        Pattern reTerm = Pattern.compile("<td>(.+?)</td>");
        Pattern reLang = Pattern.compile(patternLang);

        String srcLang, trgLang;
        srcLang = trgLang = null;

        Expression exp = new Expression();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            Matcher mBr = reExample.matcher(line);
            Matcher mLang = reLang.matcher(line);

            if (mLang.find()) {
                String[] languages = this.parseLanguages(mLang.group(1));
                srcLang = languages[0];
                trgLang = languages[1];

            } else if (mBr.find()) {
                if (exp.getTerm() != null) {
                    // continue to fill

                    String[] parts = line.split(patternExample);

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
                exp = new Expression();
                exp.setSrcLang(srcLang);
                exp.setTrgLang(trgLang);

                if (mTerm.find()) {
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

    public String[] parseLanguages(String langString) throws MessageParserException {
        String[] parts = langString.split("&gt;");

        if (parts.length == 2) {
            parts[0] = parts[0].trim().toLowerCase();
            parts[1] = parts[1].trim().toLowerCase();
            return parts;

        } else {
            throw new MessageParserException("Lang specification is invalid");
        }
    }

}
