package com.goldfinch.bunker.player.listener;

import com.goldfinch.bunker.Bunker;
import com.goldfinch.bunker.bungee.Bungee;
import com.goldfinch.bunker.player.obj.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerData playerData = Bunker.getPlayerData(player.getUniqueId());

        if (playerData == null)
            new PlayerData(player.getUniqueId());

        if (playerData.isHasLeaved()) {
            Bunker.getInstance().getServers().values().forEach(bunkerServer -> {
                if (bunkerServer.getLeavedPlayers().containsKey(player.getUniqueId().toString()))
                    Bungee.sendPlayer(player, bunkerServer.getServerName());
                else
                    player.getInventory().clear();
            });
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerData playerData = Bunker.getPlayerData(player.getUniqueId());

        playerData.saveData();
    }
}
