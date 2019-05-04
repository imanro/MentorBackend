package mentor.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
class ExpressionRepositoryTest {

    @Autowired
    private ExpressionRepository expressionRepository;

    @Autowired
    private HitRepository hitRepository;


    @BeforeEach
    void clean() {
        expressionRepository.deleteAll();
        hitRepository.deleteAll();
    }

    @Test
    void objectSaved() {

        // The main thing is we have to configure out application as spring to test it as spring
        Expression exp = new Expression();

        exp.setTerm("a");
        exp.setSrcLang("english");
        exp.setTrgLang("russian");
        exp.setExample("a in string");
        exp.setTranslation("a is b");

        String checkHash = exp.getHash();

        // save it
        expressionRepository.save(exp);

        List<Expression> collection = expressionRepository.findAll();
        assertThat("The collection size is wrong", collection.size(), is(1));
        Expression found = collection.get(0);
        assertThat("The found document is not the one we expect", found.getHash(), is(checkHash));
    }

    @Test
    void canFindByHash() {
        Expression exp1 = new Expression();
        exp1.setTerm("a");
        String checkHash = exp1.getHash();

        // save it
        expressionRepository.save(exp1);

        Expression exp2 = new Expression();
        exp2.setTerm("b");

        expressionRepository.save(exp2);

        Expression found = expressionRepository.findByHash(checkHash);
        assertNotNull(found, "We have not found the expression");
        assertEquals(found.getHash(), checkHash, "The hashes are not matched");
    }

    @Test
    void canFindBySrcLangAndTrgLang() {

        // create mentor.expression
        Expression exp1 = new Expression();
        exp1.setTerm("Term 1");
        exp1.setSrcLang("chinese");
        exp1.setTrgLang("chinese");

        // save it
        expressionRepository.save(exp1);


        Expression exp2 = new Expression();
        exp2.setTerm("Term 2");
        exp2.setSrcLang("english");
        exp2.setTrgLang("ukrainian");

        String checkHash = exp2.getHash();
        expressionRepository.save(exp2);

        Expression exp3 = new Expression();
        exp3.setTerm("Term 3");
        exp3.setSrcLang("esperanto");
        exp3.setTrgLang("esperanto");
        expressionRepository.save(exp3);


        List<Expression> collection = expressionRepository.findAllBySrcLangAndTrgLang("english", "ukrainian");
        assertThat("The collection size is not right", collection.size(), is(1));
        Expression found = collection.get(0);
        assertThat("The document is not the one we expect", found.getHash(), is(checkHash));
    }

    @Test
    void canFindBySrcLangAndTrgLangWithSortingAndPaging() {

        // create mentor.expression
        Expression exp1 = new Expression();
        exp1.setTerm("Term 4");
        exp1.setSrcLang("english");
        exp1.setTrgLang("ukrainian");

        // save it
        expressionRepository.save(exp1);


        Expression exp2 = new Expression();
        exp2.setTerm("Term 2");
        exp2.setSrcLang("english");
        exp2.setTrgLang("ukrainian");

        expressionRepository.save(exp2);

        Expression exp3 = new Expression();
        exp3.setTerm("Term 1");
        exp3.setSrcLang("english");
        exp3.setTrgLang("ukrainian");

        String checkHash1 = exp3.getHash();
        expressionRepository.save(exp3);

        Expression exp4 = new Expression();
        exp4.setTerm("Term 3");
        exp4.setSrcLang("english");
        exp4.setTrgLang("ukrainian");

        expressionRepository.save(exp4);

        List<Expression> collection = expressionRepository.findAllBySrcLangAndTrgLang("english", "ukrainian", PageRequest.of(0, 2, Sort.by("term").ascending()));
        assertThat("The collection size is wrong", collection.size(), is(2));
        Expression found = collection.get(0);
        assertThat("The first document is not the one we expect", found.getHash(), is(checkHash1));
    }

    @Test
    void canFindExpressionsWithHitsLessThanAmount() {

        // create mentor.expression
        Expression exp1 = new Expression();
        exp1.setTerm("Term 1");
        exp1.setSrcLang("english");
        exp1.setTrgLang("ukrainian");

        // save it
        expressionRepository.save(exp1);

        Expression exp2 = new Expression();
        exp2.setTerm("Term 2");
        exp2.setSrcLang("english");
        exp2.setTrgLang("ukrainian");

        expressionRepository.save(exp2);

        Expression exp3 = new Expression();
        exp3.setTerm("Term 3");
        exp3.setSrcLang("english");
        exp3.setTrgLang("ukrainian");

        expressionRepository.save(exp3);

        List<Expression> foundAllExpressions = expressionRepository.findAll();
        assertThat("The amount of stored expressions should be 3", foundAllExpressions.size(), is(3));

        Hit hit1 = new Hit();
        hit1.setExpressionHash(exp1.getHash());
        hitRepository.save(hit1);
        hitRepository.save(hit1);

        Hit hit2 = new Hit();
        hit2.setExpressionHash(exp2.getHash());
        hitRepository.save(hit2);

        List<Hit> foundHits = hitRepository.findAll();

        assertThat("The amount of stored hits should be 3", foundHits.size(), is(3));

        List<Expression> foundExpressions = expressionRepository.findAllBySrcLangAndTrgLangWithHitsLte("english", "ukrainian", 1);
        assertThat("The amount of found expressions should be 2", foundExpressions.size(), is(2));

        List<Expression> foundExpressionsPage1 = expressionRepository.findAllBySrcLangAndTrgLangWithHitsLte("english", "ukrainian", 1, PageRequest.of(1, 1));
        assertThat("The amount of found (pageable) expressions should be 1", foundExpressionsPage1.size(), is(1));
    }

    @Test
    void canFindExpressionsWithHitsLessThanAmountSortedByHits() {

        // create mentor.expression
        Expression exp1 = new Expression();
        exp1.setTerm("Term 1");
        exp1.setSrcLang("english");
        exp1.setTrgLang("ukrainian");

        // save it
        expressionRepository.save(exp1);

        Expression exp2 = new Expression();
        exp2.setTerm("Term 2");
        exp2.setSrcLang("english");
        exp2.setTrgLang("ukrainian");

        expressionRepository.save(exp2);

        Expression exp3 = new Expression();
        exp3.setTerm("Term 3");
        exp3.setSrcLang("english");
        exp3.setTrgLang("ukrainian");

        expressionRepository.save(exp3);

        List<Expression> foundAllExpressions = expressionRepository.findAll();
        assertThat(String.format("The amount of all stored expressions should be \"%s\"", 3), foundAllExpressions.size(), is(3));

        Hit hit1 = new Hit();
        hit1.setExpressionHash(exp1.getHash());
        hitRepository.save(hit1);
        hitRepository.save(hit1);
        hitRepository.save(hit1);

        Hit hit2 = new Hit();
        hit2.setExpressionHash(exp2.getHash());
        hitRepository.save(hit2);
        hitRepository.save(hit2);

        List<Hit> foundHits = hitRepository.findAll();

        assertThat(String.format("The amount of stored hits should be \"%d\"", 5), foundHits.size(), is(5));

        List<Expression> foundExpressions = expressionRepository.findAllBySrcLangAndTrgLangWithHitsLte("english", "ukrainian", 3);
        assertThat(String.format("The amount of found expressions should be \"%s\"", 3), foundExpressions.size(), is(3));

        Expression checkExpression = foundExpressions.get(0);
        assertEquals(3, checkExpression.getHitsAmount(), String.format("Expression result should be sorted so that this expression should contain \"%d\" hits", 3));
    }

    @Test
    void canFindExpressionsWithHitsLessThanAmountByLanguage() {

        // create mentor.expression
        Expression exp1 = new Expression();
        exp1.setTerm("Term 1");
        exp1.setSrcLang("english");
        exp1.setTrgLang("ukrainian");

        // save it
        expressionRepository.save(exp1);

        List<Expression> foundAllExpressions = expressionRepository.findAll();
        assertThat("The amount of stored expressions should be 1", foundAllExpressions.size(), is(1));

        Hit hit1 = new Hit();
        hit1.setExpressionHash(exp1.getHash());
        hitRepository.save(hit1);

        List<Hit> foundHits = hitRepository.findAll();

        assertThat("The amount of stored hits should be 1", foundHits.size(), is(1));

        List<Expression> foundExpressions = expressionRepository.findAllBySrcLangAndTrgLangWithHitsLte("english", "ukrainian", 1);
        assertThat("The amount of found expressions should be 1", foundExpressions.size(), is(1));


        List<Expression> foundExpressionsWrongLanguage = expressionRepository.findAllBySrcLangAndTrgLangWithHitsLte("french", "ukrainian", 1);
        assertThat("The amount of found expressions with wrong language should be 0", foundExpressionsWrongLanguage.size(), is(0));
    }

    @Test
    void canFindExpressionsWithHitsLessThanAmountPropertiesExists() {

        // create mentor.expression
        Expression exp1 = new Expression();
        exp1.setTerm("Term 1");
        exp1.setSrcLang("english");
        exp1.setTrgLang("ukrainian");
        exp1.setExample("a");
        exp1.setTranslation("b");

        // save it
        expressionRepository.save(exp1);

        List<Expression> foundAllExpressions = expressionRepository.findAll();
        assertThat("The amount of stored expressions should be 1", foundAllExpressions.size(), is(1));

        Hit hit1 = new Hit();
        hit1.setExpressionHash(exp1.getHash());
        hitRepository.save(hit1);

        List<Hit> foundHits = hitRepository.findAll();

        assertThat("The amount of stored hits should be 1", foundHits.size(), is(1));

        List<Expression> foundExpressions = expressionRepository.findAllBySrcLangAndTrgLangWithHitsLte("english", "ukrainian", 1);
        assertThat("The amount of found expressions should be 1", foundExpressions.size(), is(1));

        Expression checkExpression = foundExpressions.get(0);

        assertEquals("Term 1", checkExpression.getTerm(), String.format("The \"%s\" property either hasn't been saved or obtained", "term"));
        assertEquals("english", checkExpression.getSrcLang(), String.format("The \"%s\" property either hasn't been saved or obtained", "srcLang"));
        assertEquals("ukrainian", checkExpression.getTrgLang(), String.format("The \"%s\" property either hasn't been saved or obtained", "trgLang"));
        assertEquals("a", checkExpression.getExample(), String.format("The \"%s\" property either hasn't been saved or obtained", "example"));
        assertEquals("b", checkExpression.getTranslation(), String.format("The \"%s\" property either hasn't been saved or obtained", "translation"));
        assertEquals(1, checkExpression.getHitsAmount(), String.format("The \"%s\" property hasn't been obtained", "hitsAmount"));
    }
}