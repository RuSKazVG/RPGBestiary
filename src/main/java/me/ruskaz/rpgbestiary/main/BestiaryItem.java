package me.ruskaz.rpgbestiary.main;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.*;

public class BestiaryItem {
    private final String id;
    private final String name;
    private final Material type;
    private final int position;
    private final List<String> lore;
    private final int model;
    private final String playerHead;

    public BestiaryItem(String key) {
        ConfigurationSection config = RPGBestiary.bestiaryEntries.getConfigurationSection(key);
        this.id = key.split("\\.")[1];
        this.name = Objects.requireNonNullElse(config.getString("name"), "Default Name");
        this.type = Material.getMaterial(Objects.requireNonNullElse(config.getString("id"), "SKELETON_SKULL"));
        this.position = config.getInt("pos");
        this.lore = config.getStringList("lore");
        this.model = config.getInt("model");
        this.playerHead = RPGBestiary.bestiaryEntries.getString(key + "headSkin");
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(RPGBestiary.transformString(name));
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(RPGBestiary.transformString(line));
        }
        meta.setLore(newLore);
        meta.setCustomModelData(model);

        if (playerHead != null) { // Honestly, I am not that advanced to actually code that, so I asked ChatGPT to do it. But hey, it works!
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            String playerHeadUrl = "http://textures.minecraft.net/texture/" + this.playerHead;
            byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", playerHeadUrl).getBytes());
            profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

            try {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    public int getPosition() {
        return position;
    }

    public Material getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getModel() {
        return model;
    }

    public String getId() {
        return id;
    }

    public String getPlayerHead() {
        return playerHead;
    }
}
