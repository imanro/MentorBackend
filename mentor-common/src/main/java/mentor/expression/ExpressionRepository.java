package mentor.expression;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpressionRepository extends MongoRepository<Expression, String> {

    List<Expression> findAllBySrcLangAndTrgLang(String srcLang, String trgLang);

    List<Expression> findAllBySrcLangAndTrgLang(String srcLang, String trgLang, Pageable pageable);

    Expression findByHash(String hash);
}
