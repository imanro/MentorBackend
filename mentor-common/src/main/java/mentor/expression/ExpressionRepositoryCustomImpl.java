package mentor.expression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
public class ExpressionRepositoryCustomImpl implements ExpressionRepositoryCustom  {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ExpressionRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // Find expressions, that has not more than "*" hits

    /*
    db.expression.aggregate([
     {$sort: { createDate : -1 }},
     {$lookup: {from: "hit", localField: "_id", foreignField: "expressionHash", as: "hits"}},
     {$addFields: {hitsAmount: {$cond: {if: {$isArray: "$hits"}, then: {$size: "$hits"}, else: "0"}}}},
     {$match: {hitsAmount: {$lte : 5}}}
     ])
   */
    @Override
    public List<Expression> findAllBySrcLangAndTrgLangWithHitsLte(String srcLang, String trgLang, Integer hitsAmount) {

        List<AggregationOperation> list = this.createBaseAggregationList(srcLang, trgLang, hitsAmount);

        TypedAggregation<Expression> agg = Aggregation.newAggregation(Expression.class, list);

        return mongoTemplate.aggregate(agg, Expression.class, Expression.class).getMappedResults();
    }

    @Override
    public List<Expression> findAllBySrcLangAndTrgLangWithHitsLte(String srcLang, String trgLang, Integer hitsAmount, Pageable pageable) {

        List<AggregationOperation> list = this.createBaseAggregationList(srcLang, trgLang, hitsAmount);

        list.add(Aggregation.skip((long)pageable.getPageNumber() * pageable.getPageSize()));
        list.add(Aggregation.limit(pageable.getPageSize()));

        TypedAggregation<Expression> agg = Aggregation.newAggregation(Expression.class, list);


        return mongoTemplate.aggregate(agg, Expression.class, Expression.class).getMappedResults();
    }

    private List<AggregationOperation> createBaseAggregationList(String srcLang, String trgLang, Integer hitsAmount) {
        List<AggregationOperation> list = new ArrayList<>();
        list.add(Aggregation.match(Criteria.where("srcLang").is(srcLang).and("trgLang").is(trgLang)));
        // !
        // list.add(Aggregation.sort(Sort.Direction.DESC, "createDate"));
        list.add(Aggregation.lookup("hit", "_id", "expressionHash", "hits"));

        ConditionalOperators.Cond condOperation = ConditionalOperators.when(
                ArrayOperators.IsArray.isArray("hits"))
                .thenValueOf(ArrayOperators.Size.lengthOfArray("hits"))
                .otherwise(0);

        list.add(Aggregation.project("hash", "createDate", "term", "example", "translation", "srcLang", "trgLang").and(condOperation).as("hitsAmount"));
        list.add(Aggregation.match(Criteria.where("hitsAmount").lte(hitsAmount)));
        // list.add(Aggregation.sort(Sort.Direction.DESC, "createDate"));
        // we filter documents that has more than x hits, but then put oftener showed into top
        list.add(Aggregation.sort(Sort.Direction.DESC, "hitsAmount").and(Sort.Direction.DESC, "createDate"));
        // list.add(Aggregation.sort(Sort.Direction.DESC, "hitsAmount"));

        return list;
    }
}
