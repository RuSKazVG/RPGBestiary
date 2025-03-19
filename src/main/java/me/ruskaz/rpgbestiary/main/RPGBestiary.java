package me.ruskaz.rpgbestiary.main;

import me.ruskaz.rpgbestiary.api.ConfigManager;
import me.ruskaz.rpgbestiary.api.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class RPGBestiary extends JavaPlugin {

    public static Plugin plugin;
    public static FileConfiguration config;
    public static ConfigManager configManager;

    public static FileConfiguration bestiaryEntries;
    public static HashMap<UUID, Integer> bestiaryPage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;

        DatabaseManager.openDatabase();

        configManager = new ConfigManager();

        Bukkit.getPluginManager().registerEvents(new Events(), this);

        Bukkit.getServer().getPluginCommand("rpgbestiary").setExecutor(new Command());

        bestiaryPage = new HashMap<>();
    }

    @Override
    public void onDisable() {
        DatabaseManager.closeDatabase();
    }

    public static String transformString(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
