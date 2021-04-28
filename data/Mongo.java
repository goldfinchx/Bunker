package com.goldfinch.bunker.data;

import com.goldfinch.bunker.Bunker;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

public class Mongo {

    @Getter private final MongoDatabase db;
    @Getter private final MongoClient mongoClient;

    public Mongo() {
        MongoClientURI mongoClientURI;
        mongoClientURI = new MongoClientURI(Bunker.getInstance().getConfig().getString("mongo.url"));

        this.mongoClient = new MongoClient(mongoClientURI);
        db = mongoClient.getDatabase(Bunker.getInstance().getConfig().getString("mongo.dbName"));
    }

    public MongoCollection<Document> getCollection(String name) { return db.getCollection(Bunker.getInstance().getConfig().getString("mongo.prefix") + name); }
}