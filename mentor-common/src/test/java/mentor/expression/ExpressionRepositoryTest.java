package mentor.expression;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

        // create mentor.expression
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

        List<Expression> collection = repository.findAll("english", "russian", 1, 0);
        assertThat(collection.size(), is(1));
        Expression found = collection.get(0);
        assertThat(found.getHash(), is(checkHash));
    }
}