package mentor.expression;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ExpressionRepository {


    private MongoClient client;
    private MongoDatabase database;

    public List<Expression>
    findAll(String srcLang, String trgLang, int limit, int offset) {
        MongoCollection<Document> collection = getCollectionExpression();
        List<Expression> items = new ArrayList<>();

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("srcLang", srcLang);
        searchQuery.put("trgLang", trgLang);

        MongoCursor<Document> cursor = collection.find(searchQuery).limit(limit).skip(offset).sort(new BasicDBObject("createDate", 1)).iterator();

        try {
            while (cursor.hasNext()) {
                Expression item = toExpression(cursor.next());
                items.add(item);
                // System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public void save(Expression expression) {
        MongoCollection<Document> collection = getCollectionExpression();
        Document dbObject = ExpressionRepository.toDocument(expression);

        try {
            collection.insertOne(dbObject);
        } catch(MongoWriteException e) {
            System.err.println("probably, duplicate key");
        }
    }

    public void drop() {
        getCollectionExpression().drop();
    }

    public void initClient() {
        client = MongoClients.create("mongodb://localhost:27017");
    }

    public void initDatabase(String name) {
        if (client == null) {
            throw new ExpressionRepositoryException("The mongodb client is not initialized yet");
        }

        database = client.getDatabase(name);
    }

    private MongoCollection<Document> getCollectionExpression() throws ExpressionRepositoryException {

        if (client == null) {
            throw new ExpressionRepositoryException("The mongodb client is not initialized yet");
        }

        if (database == null) {
            throw new ExpressionRepositoryException("The mongodb database is not initialized yet");
        }

        return database.getCollection("expression");
    }

    public static final Document toDocument(Expression expression) {
        return new Document("_id", expression.getHash())
                .append("hash", expression.getHash())
                .append("term", expression.getTerm())
                .append("srcLang", expression.getSrcLang())
                .append("trgLang", expression.getTrgLang())
                .append("example", expression.getExample())
                .append("createDate", expression.getCreateDate())
                .append("translation", expression.getTranslation());
    }

    public static final Expression toExpression(Document document) {
        Expression item = new Expression();
        item.setTerm(document.getString("term"));
        item.setExample(document.getString("example"));
        item.setTranslation(document.getString("translation"));
        item.setHash(document.getString("hash"));
        item.setCreateDate(document.getDate("createDate"));
        item.setSrcLang(document.getString("srcLang"));
        item.setTrgLang(document.getString("trgLang"));
        return item;
    }

}
