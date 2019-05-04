package mentor.expression;

        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
        import org.springframework.data.domain.PageRequest;
        import org.springframework.data.domain.Sort;


        import java.util.List;

        import static org.hamcrest.MatcherAssert.assertThat;
        import static org.hamcrest.Matchers.is;


// This configuration will create connection to real database, defined in src/test/java/application.properties
// @DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
// And, this configuration will use in-memory db connector, that works slightly faster
@DataMongoTest
class HitRepositoryTest {

    @Autowired
    private HitRepository hitRepository;

    @Autowired
    private ExpressionRepository expressionRepository;

    @BeforeEach
    void clean() {
        hitRepository.deleteAll();
        expressionRepository.deleteAll();
    }

    @Test
    void objectSaved() {

        Expression exp = new Expression();
        exp.setTerm("a");
        String checkHash = exp.getHash();

        expressionRepository.save(exp);

        Hit hit = new Hit();
        hit.setExpressionHash(exp.getHash());

        hitRepository.save(hit);

        List<Hit> collection = hitRepository.findAll();
        assertThat("The collection size is wrong", collection.size(), is(1));
        Hit found = collection.get(0);
        assertThat("The referenced document is not the one we expect", found.getExpressionHash(), is(checkHash));
    }

    @Test
    void canFindHitsByHash() {

        Expression exp1 = new Expression();
        exp1.setTerm("a");
        String checkHash = exp1.getHash();

        expressionRepository.save(exp1);

        Expression exp2 = new Expression();
        exp2.setTerm("b");
        expressionRepository.save(exp2);

        Hit hit1 = new Hit();
        hit1.setExpressionHash(exp1.getHash());
        hitRepository.save(hit1);


        Hit hit2 = new Hit();
        hit2.setExpressionHash(exp2.getHash());
        hitRepository.save(hit2);

        List<Hit> collection = hitRepository.findAllByExpressionHash(checkHash);
        assertThat("The collection size is wrong", collection.size(), is(1));
        Hit found = collection.get(0);

        System.out.println("Searching for " + checkHash);
        System.out.println("Got " + found.getExpressionHash());
        assertThat("The referenced document is not the one we expect", found.getExpressionHash(), is(checkHash));
    }
}