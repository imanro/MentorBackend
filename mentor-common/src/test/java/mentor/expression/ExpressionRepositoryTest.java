package mentor.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


// This configuration will create connection to real database, defined in src/test/java/application.properties
// @DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
// And, this configuration will use in-memory db connector, that works slightly faster
@DataMongoTest
class ExpressionRepositoryTest {

    @Autowired
    private ExpressionRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void objectSaved() {

        /* The main thing is we have to configure out application as spring to test it as spring */
        Expression exp = new Expression();

        exp.setTerm("a");
        exp.setSrcLang("english");
        exp.setTrgLang("russian");
        exp.setExample("a in string");
        exp.setTranslation("a is b");

        String checkHash = exp.getHash();

        // save it
        repository.save(exp);

        List<Expression> collection = repository.findAll();
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
        repository.save(exp1);

        Expression exp2 = new Expression();
        exp2.setTerm("b");

        repository.save(exp2);

        Expression found = repository.findByHash(checkHash);
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
        repository.save(exp1);


        Expression exp2 = new Expression();
        exp2.setTerm("Term 2");
        exp2.setSrcLang("english");
        exp2.setTrgLang("ukrainian");

        String checkHash = exp2.getHash();
        repository.save(exp2);

        Expression exp3 = new Expression();
        exp3.setTerm("Term 3");
        exp3.setSrcLang("esperanto");
        exp3.setTrgLang("esperanto");
        repository.save(exp3);


        List<Expression> collection = repository.findAllBySrcLangAndTrgLang("english", "ukrainian");
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
        repository.save(exp1);


        Expression exp2 = new Expression();
        exp2.setTerm("Term 2");
        exp2.setSrcLang("english");
        exp2.setTrgLang("ukrainian");

        repository.save(exp2);

        Expression exp3 = new Expression();
        exp3.setTerm("Term 1");
        exp3.setSrcLang("english");
        exp3.setTrgLang("ukrainian");

        String checkHash1 = exp3.getHash();
        repository.save(exp3);

        Expression exp4 = new Expression();
        exp4.setTerm("Term 3");
        exp4.setSrcLang("english");
        exp4.setTrgLang("ukrainian");

        repository.save(exp4);

        List<Expression> collection = repository.findAllBySrcLangAndTrgLang("english", "ukrainian", PageRequest.of(0, 2, Sort.by("term").ascending()));
        assertThat("The collection size is wrong", collection.size(), is(2));
        Expression found = collection.get(0);
        assertThat("The first document is not the one we expect", found.getHash(), is(checkHash1));
    }
}