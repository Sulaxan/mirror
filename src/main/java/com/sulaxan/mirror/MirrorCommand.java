package com.sulaxan.mirror;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class MirrorCommand implements CommandExecutor {

    private final MirrorManager manager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Not a player");
            return false;
        }

        var player = (Player) sender;
        manager.constructNewBox(player.getLocation());
        player.sendMessage(ChatColor.GREEN.toString() + "Constructed mirror box");

        return true;
    }
}
