package com.goldfinch.bunker.commands;

import com.goldfinch.bunker.Bunker;
import com.goldfinch.bunker.managing.obj.BunkerServer;
import com.goldfinch.bunker.bungee.Bungee;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand extends Command {
    public TestCommand() {
        super("test");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        Player player = (Player) commandSender;

        for (BunkerServer bunkerServer : Bunker.getInstance().getServers().values())
            if (bunkerServer.getLeavedPlayers().containsKey(player.getUniqueId().toString())) {
                Bungee.sendPlayer(player, bunkerServer.getServerName());
                return false;
            }

        for (BunkerServer bunkerServer : Bunker.getInstance().getServers().values())
            if (bunkerServer.getState().equals("COUNTDOWN") || bunkerServer.getState().equals("RECRUITING")) {
                Bungee.sendPlayer(player, bunkerServer.getServerName());
                return false;
            }

        return false;
    }
}
