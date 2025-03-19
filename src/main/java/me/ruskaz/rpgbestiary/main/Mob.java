package me.ruskaz.rpgbestiary.main;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public record Mob(EntityType mobType, String customName) {
    public Mob(EntityType mobType, String customName) {
        this.mobType = mobType;
        if (customName != null) {
            this.customName = ChatColor.translateAlternateColorCodes('&', customName);
        } else {
            this.customName = "";
        }
    }

    @Override
    public String toString() {
        return mobType().toString() + " " + customName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Mob mob)) return false;
        return mobType == mob.mobType() && customName.equals(mob.customName());
    }
}
