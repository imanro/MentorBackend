package mentor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;


class ExpressionRepositoryTest {

    private static ExpressionRepository repository;

    @BeforeAll
    public static void init() {
        repository = new ExpressionRepository();
        repository.initDatabase("mentorTest");
        repository.initClient();
    }

    @Test
    void objectSaved() {
        repository.drop();

        // create expression
        Expression exp = new Expression();
        exp.setTerm("a");
        exp.setExample("a in string");
        exp.setTranslation("a is b");
        exp.createHash();

        // save it
        repository.save(exp);

    }
}