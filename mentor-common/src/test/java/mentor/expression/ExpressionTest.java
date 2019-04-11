package mentor.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class ExpressionTest {

    Expression expression;

    @BeforeEach
    public void init() {
        expression = new Expression();
    }

    @Test
    void hashGenerated() {
        String term = "Term";
        assertEquals("CF5F3091E30DEE6597885D8C0E0C357F", expression.generateHash(term), "The strings should be equal");
    }

    @Test
    void hashCreated() {
        String term = "Term";
        expression.setTerm(term);
        assertNotNull(expression.getHash(), "Should not be null");
    }

    @Test
    void hashesShouldDiffers() {
        String term = "Term";
        expression.setTerm(term);

        String hash1 = expression.getHash();

        String example = "Term2";
        expression.setExample(example);

        String hash2 = expression.getHash();

        assertNotEquals(hash1, hash2, "The strings should not be equal");
    }
}