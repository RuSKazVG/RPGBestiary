package me.ruskaz.rpgbestiary.main;

import me.ruskaz.rpgbestiary.api.BestiaryManager;
import me.ruskaz.rpgbestiary.api.ConfigManager;
import me.ruskaz.rpgbestiary.api.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {
        if (args.length == 3) {
            if (commandSender.hasPermission("rpgbestiary.modify")) {
                if (args[0].equalsIgnoreCase("remove")) {
                    UUID uuid;
                    Player p = Bukkit.getPlayer(args[1]);
                    if (p == null) {
                        uuid = UUID.fromString(args[1]);
                    } else {
                        uuid = p.getUniqueId();
                    }
                    String list = DatabaseManager.getPlayerKilledLine(uuid);
                    if (list.contains(args[2])) {
                        DatabaseManager.removeMobFromDatabase(uuid, args[2]);
                        String message = RPGBestiary.transformString(RPGBestiary.configManager.getBestiaryMobRemovedMessage().replaceAll("%mob_name%", args[2]).replaceAll("%player%", args[1]));
                        commandSender.sendMessage(message);
                    } else {
                        String message = RPGBestiary.transformString(RPGBestiary.configManager.getBestiaryMobNotRemovedMessage().replaceAll("%mob_name%", args[2]).replaceAll("%player%", args[1]));
                        commandSender.sendMessage(message);
                    }
                } else if (args[0].equalsIgnoreCase("add")) {
                    UUID uuid;
                    Player p = Bukkit.getPlayer(args[1]);
                    if (p == null) {
                        uuid = UUID.fromString(args[1]);
                    } else {
                        uuid = p.getUniqueId();
                    }
                    String list = DatabaseManager.getPlayerKilledLine(uuid);
                    if (!list.contains(args[2])) {
                        String message = RPGBestiary.transformString(RPGBestiary.configManager.getBestiaryMobAddedMessage().replaceAll("%mob_name%", args[2]).replaceAll("%player%", args[1]));
                        commandSender.sendMessage(message);
                    } else {
                        String message = RPGBestiary.transformString(RPGBestiary.configManager.getBestiaryMobNotAddedMessage().replaceAll("%mob_name%", args[2]).replaceAll("%player%", args[1]));
                        commandSender.sendMessage(message);
                    }
                }
                return true;
            }
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("reload") && commandSender.hasPermission("rpgbestiary.reload")) {
            RPGBestiary.configManager = new ConfigManager();
            return true;
        }
        else {
            if (commandSender instanceof Player p) {
                BestiaryManager.openBestiary(p, 1);
                return true;
            }
        }
        return false;
    }
}
