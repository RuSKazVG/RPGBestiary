package me.ruskaz.rpgbestiary.api;

import me.ruskaz.rpgbestiary.main.BestiaryItem;
import me.ruskaz.rpgbestiary.main.Mob;
import me.ruskaz.rpgbestiary.main.MythicEvents;
import me.ruskaz.rpgbestiary.main.RPGBestiary;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {

    private final boolean mythicMobsSupport;
    private final boolean showUnrevealedMobs;


    private final String bestiaryName;
    private final int bestiarySize;
    private final List<Integer> ignoredPosList;

    private final String bestiaryKillMessage;

    private final List<BestiaryItem> plugList;
    private final List<BestiaryItem> mobItemsList;
    private final HashMap<Mob, String> mobTypeList;
    private final HashMap<String, String> mythicMobsList;

    private final BestiaryItem nextPageItem;
    private final BestiaryItem previousPageItem;
    private final BestiaryItem unrevealedMobItem;

    public ConfigManager() {
        Plugin plugin = RPGBestiary.plugin;
        plugin.reloadConfig();
        RPGBestiary.config = plugin.getConfig();
        FileConfiguration config = RPGBestiary.config;

        File bestiaryFile = new File(plugin.getDataFolder(), "BestiaryEntries.yml");
        if (!bestiaryFile.exists()) {
            try {
                bestiaryFile.createNewFile();
                RPGBestiary.plugin.saveResource("BestiaryEntries.yml", true);
            }
            catch (IOException e) { e.printStackTrace(); throw new RuntimeException(e); }
        }
        RPGBestiary.bestiaryEntries = YamlConfiguration.loadConfiguration(bestiaryFile);

        this.mythicMobsSupport = config.getBoolean("mythicMobsSupport");
        this.showUnrevealedMobs = config.getBoolean("showUnrevealedMobs");

        this.bestiaryName = config.getString("bestiarySettings.name");
        this.bestiarySize = config.getInt("bestiarySettings.size");
        this.ignoredPosList = config.getIntegerList("bestiarySettings.ignoredPositions");

        this.bestiaryKillMessage = config.getString("bestiaryKillMessage");

        this.plugList = new ArrayList<>();
        this.mobItemsList = new ArrayList<>();

        this.mobTypeList = new HashMap<>();
        this.mythicMobsList = new HashMap<>();


        FileConfiguration bestiaryConfig = RPGBestiary.bestiaryEntries;

        //this.sortingMethodItem = new BestiaryItem("defaultItems.sortingMethod.");
        this.nextPageItem = new BestiaryItem("defaultItems.nextPage.");
        this.previousPageItem = new BestiaryItem("defaultItems.prevPage.");
        this.unrevealedMobItem = new BestiaryItem("defaultItems.unrevealedMob.");

        for (String key : bestiaryConfig.getConfigurationSection("bestiaryItems").getKeys(false)) {
            this.plugList.add(new BestiaryItem(("bestiaryItems." + key + ".")));
        }

        ConfigurationSection mobSection = bestiaryConfig.getConfigurationSection("bestiaryMobs");
        for (String key : mobSection.getKeys(false)) {
            this.mobItemsList.add(new BestiaryItem(("bestiaryMobs." + key + ".itemInfo.")));
            if (mobSection.getString(key + ".mobInfo.mythicMobType") == null) this.mobTypeList.put(new Mob(EntityType.valueOf(mobSection.getString(key + ".mobInfo.mobType").toUpperCase()), mobSection.getString(key + ".mobInfo.name")), key);
            else this.mythicMobsList.put(mobSection.getString(key + ".mobInfo.mythicMobType"), key);
        }

        if (isMythicMobsSupported()) {
            Bukkit.getPluginManager().registerEvents(new MythicEvents(), RPGBestiary.plugin);
        }
    }

    public boolean isMythicMobsSupported() {
        return mythicMobsSupport;
    }

    public boolean showUnrevealedMobs() {
        return showUnrevealedMobs;
    }

    public String getBestiaryName() {
        return bestiaryName;
    }

    public int getBestiarySize() {
        return bestiarySize;
    }

    public List<Integer> getIgnoredPosList() {
        return ignoredPosList;
    }

    public String getBestiaryKillMessage() {
        return bestiaryKillMessage;
    }

    public List<BestiaryItem> getPlugList() {
        return plugList;
    }

    public List<BestiaryItem> getMobItemsList() {
        return mobItemsList;
    }

    public HashMap<Mob, String> getMobTypeList() {
        return mobTypeList;
    }

    public HashMap<String, String> getMythicMobsList() {
        return mythicMobsList;
    }

    //public BestiaryItem getSortingMethodItem() {
    //    return sortingMethodItem;
    //}

    public BestiaryItem getNextPageItem() {
        return nextPageItem;
    }

    public BestiaryItem getPreviousPageItem() {
        return previousPageItem;
    }

    public BestiaryItem getUnrevealedMobItem() {
        return unrevealedMobItem;
    }
}
