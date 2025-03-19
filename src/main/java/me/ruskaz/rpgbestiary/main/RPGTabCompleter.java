package me.ruskaz.rpgbestiary.main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RPGTabCompleter implements org.bukkit.command.TabCompleter  {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        ArrayList<String> completes = new ArrayList<>();
        if (args.length == 1) {
            if (commandSender.hasPermission("rpgbestiary.reload") || commandSender.isOp()) {
                completes.add("reload");
            }
        }
        if (commandSender.hasPermission("rpgbestiary.modify") || commandSender.isOp()) {
            if (args.length == 1) {
                completes.add("add");
                completes.add("remove");
                completes.add("clear");
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        completes.add(p.getName());
                    }
                }
            }
            else if (args.length == 3 && !args[0].equalsIgnoreCase("clear") && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
                for (BestiaryItem item : RPGBestiary.configManager.getMobItemsList()) {
                    completes.add(item.getId());
                }
            }
        }
        Collections.sort(completes);
        return completes;
    }
}
