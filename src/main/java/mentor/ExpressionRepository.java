package mentor;


import com.mongodb.Block;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ExpressionRepository {

    private MongoClient client;
    private MongoDatabase database;

    public List<Expression> findAll(int limit) {
        MongoCollection<Document> collection = getCollection();
        List<Expression> items = new ArrayList<Expression>();
        MongoCursor<Document> cursor = collection.find().limit(limit).iterator();

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
        MongoCollection<Document> collection = getCollection();
        Document dbObject = ExpressionRepository.toDocument(expression);

        try {
            collection.insertOne(dbObject);
        } catch(MongoWriteException e) {
            System.err.println("probably, duplicate key");
        }
    }

    public void drop() {
        getCollection().drop();
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

    private MongoCollection<Document> getCollection() throws ExpressionRepositoryException {

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
        return item;
    }

}
