package mentor.expression;

        import java.util.List;

        import org.springframework.data.domain.Pageable;
        import org.springframework.data.mongodb.repository.MongoRepository;
        import org.springframework.data.mongodb.repository.Query;

public interface HitRepository extends MongoRepository<Hit, String> {
    List<Hit> findAllByExpressionHash(String expressionHash);
}
