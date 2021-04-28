package com.goldfinch.bunker.managing.obj;

import com.goldfinch.bunker.Bunker;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import lombok.Getter;
import org.bson.Document;

import java.util.List;

public class BunkerServer {

    @Getter
    private final static MongoCollection serversCollection =
            Bunker.getInstance().getMongo().getCollection("servers");

    @Getter private final int id;
    @Getter private final String serverName;
    @Getter private final String state;

    @Getter private List<String> players;
    @Getter private final Document leavedPlayers;

    public BunkerServer(int id) {
        this.id = id;

        BasicDBObject serverQuery = new BasicDBObject();
        serverQuery.put("_id", id);
        Document serverDocument = (Document) serversCollection.find(serverQuery).first();

        this.serverName = serverDocument.getString("serverName");
        this.state = serverDocument.getString("state");
        this.players = serverDocument.getList("players", String.class);
        this.leavedPlayers = (Document) serverDocument.get("leavedPlayers");

        Bunker.getInstance().getServers().put(players.size(), this);
    }

    public static void loadUpServers() {
        MongoCursor cursor = serversCollection.find().iterator();

        while (cursor.hasNext()) {
            Document document = (Document) cursor.next();
            new BunkerServer(document.getInteger("_id"));
        }

    }

}
