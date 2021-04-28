package com.goldfinch.bunker;

import com.goldfinch.bunker.bungee.Bungee;
import com.goldfinch.bunker.commands.TestCommand;
import com.goldfinch.bunker.data.Mongo;
import com.goldfinch.bunker.items.obj.BunkerItem;
import com.goldfinch.bunker.managing.obj.BunkerServer;
import com.goldfinch.bunker.player.listener.PlayerListener;
import com.goldfinch.bunker.player.obj.PlayerData;
import com.goldfinch.bunker.utils.ReflectionUtil;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bunker extends JavaPlugin {

    private static Bunker instance;
    public static Bunker getInstance() { return instance; }

    @Getter private Mongo mongo;
    @Getter private InventoryManager inventoryManager;

    @Getter private HashMap<UUID, PlayerData> players;
    @Getter private TreeMap<Integer, BunkerServer> servers;

    @Getter private List<BunkerItem> items;

    @Override
    public void onEnable() {
        instance = this;

        // Подключение внутреннего конфига
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        // Подключение Bungee каналов
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new Bungee());

        // Подключение MongoDB
        this.mongo = new Mongo();
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        // Подключение менеджера инвентарей
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();

        // Загрузка данных игроков
        players = new HashMap<>();
        PlayerData.loadUpPlayers();

        // Загрузка предметов
        items = new ArrayList<>();
        BunkerItem.loadUpItems();

        // Загрузка серверов
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            this.servers = new TreeMap<>();
            BunkerServer.loadUpServers();
        }, 20*5, 20*5);


        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                getPlayerData(onlinePlayer.getUniqueId()).saveData();
        }, 1200*5, 1200*5);

        // Регистрация лисенеров
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        // Регистрация команд
        ReflectionUtil.registerCommand("test", new TestCommand());

    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = getPlayerData(player.getUniqueId());
            playerData.saveData();

            player.kickPlayer("Сервер перезагружается!");
        }
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return getInstance().getPlayers().get(uuid);
    }


}
