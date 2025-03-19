package me.ruskaz.rpgbestiary.api;

import me.ruskaz.rpgbestiary.main.BestiaryItem;
import me.ruskaz.rpgbestiary.main.RPGBestiary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class BestiaryManager {


    public static void openBestiary(Player p, int page) {
        ConfigManager manager = RPGBestiary.configManager;
        Inventory bestiary = Bukkit.createInventory(p, manager.getBestiarySize(), ChatColor.translateAlternateColorCodes('&', manager.getBestiaryName()));

        String list = DatabaseManager.getPlayerKilledLine(p.getUniqueId());

        Set<Integer> takenPositions = new HashSet<>(manager.getIgnoredPosList());

        BestiaryItem nextPageItem = manager.getNextPageItem();
        BestiaryItem previousPageItem = manager.getPreviousPageItem();
        BestiaryItem unrevealedMobItem = manager.getUnrevealedMobItem();

        BestiaryItem[] defaultItems = new BestiaryItem[]{ nextPageItem, previousPageItem, unrevealedMobItem };

        for (BestiaryItem item : defaultItems) {
            bestiary.setItem(item.getPosition(), item.getItemStack());
            takenPositions.add(item.getPosition());
        }

        for (BestiaryItem item : manager.getPlugList()) {
            bestiary.setItem(item.getPosition(), item.getItemStack());
            takenPositions.add(item.getPosition());
        }

        List<BestiaryItem> mobSet = new ArrayList<>();
        mobSet.addAll(manager.getMobItemsList());
        int freeSpace = manager.getBestiarySize() - takenPositions.size();
        page = Math.min(page, (int) Math.ceil((double) mobSet.size() / freeSpace));
        RPGBestiary.bestiaryPage.put(p.getUniqueId(), page);
        int mobsToRemove = freeSpace * (page - 1);
        mobSet = mobSet.subList(mobsToRemove, mobSet.size());

        for (BestiaryItem item : mobSet) {
            for (int i = 0; bestiary.getSize() > i; i++) {
                if (!takenPositions.contains(i)) {
                    if (list.contains(item.getId())) {
                        bestiary.setItem(i, item.getItemStack());
                    }
                    else if (manager.showUnrevealedMobs()) {
                        bestiary.setItem(i, unrevealedMobItem.getItemStack());
                    }
                    takenPositions.add(i);
                    break;
                }
            }
        }

        p.openInventory(bestiary);
    }
}
