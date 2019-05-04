package mentor.expression.api;

import mentor.expression.Expression;

import mentor.expression.ExpressionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/expression")
public class ExpressionController {

    private final ExpressionRepository expressionRepository;

    public ExpressionController(ExpressionRepository expressionRepository) {
        this.expressionRepository = expressionRepository;
    }

    @GetMapping("")
    public List<Expression> getExpressions(@RequestParam(name = "srcLang", defaultValue = "english") String srcLang,
                                           @RequestParam(name = "trgLang", defaultValue = "russian") String trgLang,
                                           @RequestParam(name = "hitsAmountLte", defaultValue = "10") int hitsAmountLte,
                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {

        // return this.expressionRepository.findAllBySrcLangAndTrgLang(srcLang, trgLang, PageRequest.of(page, size, Sort.by("createDate").descending()));
        return this.expressionRepository.findAllBySrcLangAndTrgLangWithHitsLte(srcLang, trgLang, hitsAmountLte, PageRequest.of(page, size));
    }
}
