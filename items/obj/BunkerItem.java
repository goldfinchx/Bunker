package com.goldfinch.bunker.items.obj;

import com.goldfinch.bunker.Bunker;
import com.goldfinch.bunker.items.parameters.ItemRarity;
import com.goldfinch.bunker.items.parameters.ItemType;
import com.goldfinch.bunker.utils.ItemBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class BunkerItem {

    private final static MongoCollection<Document> itemsCollection =
            Bunker.getInstance().getMongo().getCollection("items");

    private BasicDBObject itemQuery;
    @Getter private final Document itemDocument;

    private int _id;
    @Getter private String title;
    @Getter private Material material;
    @Getter private ItemRarity rarity;
    @Getter private ItemType type;

    public BunkerItem(int _id, String title, Material material, ItemRarity rarity, ItemType type) {
        itemDocument = new Document();

        itemDocument.put("_id", _id);
        itemDocument.put("title", title);
        itemDocument.put("material", material.name());
        itemDocument.put("rarity", rarity.name());
        itemDocument.put("type", type.name());

        itemsCollection.insertOne(itemDocument);
        Bunker.getInstance().getItems().add(this);
    }

    public BunkerItem(int _id) {
        this._id = _id;

        this.itemQuery = new BasicDBObject();
        this.itemQuery.put("_id", _id);
        this.itemDocument = itemsCollection.find(itemQuery).first();

        this.title = itemDocument.getString("title");
        this.material = Material.getMaterial(itemDocument.getString("material"));
        this.rarity = ItemRarity.valueOf(itemDocument.getString("rarity"));
        this.type = ItemType.valueOf(itemDocument.getString("type"));

        Bunker.getInstance().getItems().add(this);
    }

    public static void loadUpItems() {
        MongoCursor cursor = itemsCollection.find().iterator();

        Bunker.getInstance().getLogger().log(Level.INFO, "Загрузка предметов.");
        Bunker.getInstance().getItems().clear();
        int items = 0;
        while (cursor.hasNext()) {
            Document document = (Document) cursor.next();
            Bunker.getInstance().getItems().add(new BunkerItem(document.getInteger("_id")));

            items++;
        }

        Bunker.getInstance().getLogger().log(Level.INFO, "Готово. Загруженно " + items + " предметов.");
    }

    public ItemStack getAsItem() {
        ItemStack item = new ItemBuilder(this.material)
                .setDisplayName(rarity.getColor() + title)
                .build();

        return item;
    }

}
