package mentor.expression.api;

import mentor.expression.ExpressionRepository;
import mentor.expression.Expression;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mentor/expression")
public class ExpressionController {

    @GetMapping("")
    public List<Expression> getExpressions(@RequestParam(name = "srcLang", defaultValue = "english") String srcLang,
                                           @RequestParam(name = "trgLang", defaultValue = "russian") String trgLang,
                                           @RequestParam(name = "limit", defaultValue = "10") int limit,
                                           @RequestParam(name = "offset", defaultValue = "0") int offset) {

        System.out.println("Called");
        return getExpressionRepository().findAll(srcLang, trgLang, limit, offset);
    }

    private static ExpressionRepository getExpressionRepository() {
        ExpressionRepository repository = new ExpressionRepository();
        repository.initClient();
        repository.initDatabase("mentor");
        return repository;
    }

}