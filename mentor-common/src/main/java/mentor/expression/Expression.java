package mentor.expression;

import javax.xml.bind.DatatypeConverter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Expression {

    @Id
    private String hash;

    @CreatedDate
    private Date createDate;

    private String term;

    private String example;

    private String translation;

    private String srcLang;

    private String trgLang;

    public Expression() {
        this.setCreateDate(new Date());
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
        this.createHash();
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
        this.createHash();
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSrcLang() {
        return srcLang;
    }

    public void setSrcLang(String srcLang) {
        this.srcLang = srcLang;
    }

    public String getTrgLang() {
        return trgLang;
    }

    public void setTrgLang(String trgLang) {
        this.trgLang = trgLang;
    }

    public void createHash() {

        String source = this.getTerm();

        if (this.getExample() != null) {
            source += this.getExample();
        }

        this.setHash(generateHash(source));
    }

    public String generateHash(String term) {
        MessageDigest generator;

        try {
            generator = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        generator.update(term.getBytes());
        byte[] digest = generator.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }
}
