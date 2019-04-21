package mentor.expression.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import mentor.expression.Expression;
import mentor.expression.ExpressionRepository;
import mentor.expression.Hit;
import mentor.expression.HitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/hit")
public class HitController {

    private final HitRepository hitRepository;

    private final ExpressionRepository expressionRepository;

    public HitController(HitRepository hitRepository, ExpressionRepository expressionRepository) {
        this.hitRepository = hitRepository;
        this.expressionRepository = expressionRepository;
    }

    @PostMapping("")
    public Map<String, String> saveHit(@RequestBody ObjectNode json) {

        String hash;

        try {
            hash = json.get("hash").textValue();
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing \"%s\" parameter", "hash"), e);
        }

        // search the expression
        Expression exp = this.expressionRepository.findByHash(hash);

        if (exp == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown expression");
        }

        // saving hit
        Hit hit = new Hit();
        hit.setExpression(exp);
        this.hitRepository.save(hit);

        return Collections.singletonMap("result", "Ok");
    }

}
