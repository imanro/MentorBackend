package mentor.expression;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

public class Hit {

    // @DBRef
    // private Expression expression;

    private String expressionHash;

    @CreatedDate
    private Date createDate;

    public Hit() {
        this.setCreateDate(new Date());
    }

    public String getExpressionHash() {
        return this.expressionHash;
    }

    public void setExpressionHash(String hash) {
        this.expressionHash = hash;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
