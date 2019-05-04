package mentor.expression;
import java.util.List;

import org.springframework.data.domain.Pageable;

public interface ExpressionRepositoryCustom {
    List<Expression> findAllBySrcLangAndTrgLangWithHitsLte(String srcLang, String trgLang, Integer hitsAmount);

    List<Expression> findAllBySrcLangAndTrgLangWithHitsLte(String srcLang, String trgLang, Integer hitsAmount, Pageable pageable);
    // + method with pageable
}
