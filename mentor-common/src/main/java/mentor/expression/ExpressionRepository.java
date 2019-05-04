package mentor.expression;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpressionRepository extends MongoRepository<Expression, String>, ExpressionRepositoryCustom {

    List<Expression> findAllBySrcLangAndTrgLang(String srcLang, String trgLang);

    List<Expression> findAllBySrcLangAndTrgLang(String srcLang, String trgLang, Pageable pageable);

    Expression findByHash(String hash);

    // Find expressions, that has not more than "*" hits

    // search exp, join hits, by hash + user (now just 1 user), sort get those which have less than x count

    // select * from exp left join hit


    // $lookup, then

    // hits: { $cond: { if: { $isArray: "$hits" }, then: { $size: "$hits" }, else: "0"} }

    // $sortByCount

   /*
    db.expression.aggregate([
     {$sort: { createDate : -1 }},
     {$lookup: {from: "hit", localField: "_id", foreignField: "expressionHash", as: "hits"}},
     {$addFields: {hitsAmount: {$cond: {if: {$isArray: "$hits"}, then: {$size: "$hits"}, else: "0"}}}},
     {$match: {hitsAmount: {$lte : 5}}}
     ])
   */
}
