package mentor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class ExpressionRepositoryTest {

    private static ExpressionRepository repository;

    @BeforeAll
    public static void init() {
        repository = new ExpressionRepository();
        repository.initClient();
        repository.initDatabase("mentorTest");
    }

    @Test
    void objectSaved() {
        repository.drop();

        // create expression
        Expression exp = new Expression();
        exp.setTerm("a");
        exp.setSrcLang("english");
        exp.setTrgLang("russian");
        exp.setExample("a in string");
        exp.setTranslation("a is b");
        exp.createHash();

        String checkHash = exp.getHash();

        // save it
        repository.save(exp);

        List<Expression> collection = repository.findAll("english", "russian", 1);
        assertThat(collection.size(), is(1));
        Expression found = collection.get(0);
        assertThat(found.getHash(), is(checkHash));
    }
}