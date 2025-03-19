package me.ruskaz.rpgbestiary.main;

import me.ruskaz.rpgbestiary.api.BestiaryManager;
import me.ruskaz.rpgbestiary.api.ConfigManager;
import me.ruskaz.rpgbestiary.api.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Events implements Listener {

    @EventHandler
    public void firstEntry(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        String list = DatabaseManager.getPlayerKilledLine(p.getUniqueId());

        if (list == null) {
            String sqlInsert = "INSERT INTO bestiary(id, list) VALUES(?, ?)";
            try (PreparedStatement pstmt = DatabaseManager.database.prepareStatement(sqlInsert)) {
                pstmt.setString(1, p.getUniqueId().toString());
                pstmt.setString(2, "sans;");
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @EventHandler
    public void addInBestiary(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;

        Mob killedMob = new Mob(e.getEntityType(), e.getEntity().getCustomName());

        boolean containsMob = false;
        for (Mob mob : RPGBestiary.configManager.getMobTypeList().keySet()) {
            if (mob.equals(killedMob)) {
                containsMob = true;
                killedMob = mob;
                break;
            }
        }

        if (!containsMob) return;

        Player p = e.getEntity().getKiller();

        String list = DatabaseManager.getPlayerKilledLine(p.getUniqueId());

        String mobId = RPGBestiary.configManager.getMobTypeList().get(killedMob);

        if (!list.contains(mobId)) {
            if (RPGBestiary.configManager.getBestiaryKillMessage() != null) {
                String bestiaryKillMessage = RPGBestiary.configManager.getBestiaryKillMessage();
                if (killedMob.customName().isEmpty()) bestiaryKillMessage = bestiaryKillMessage.replaceAll("%mob_name%", killedMob.mobType().toString().toLowerCase());
                else bestiaryKillMessage = bestiaryKillMessage.replaceAll("%mob_name%", killedMob.customName());
                bestiaryKillMessage = RPGBestiary.transformString(bestiaryKillMessage);
                p.sendMessage(bestiaryKillMessage);
            }
            DatabaseManager.addKilledMobToDatabase(p.getUniqueId(), mobId);
        }
    }

    @EventHandler
    public void preventDraggingItems(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(RPGBestiary.transformString(RPGBestiary.configManager.getBestiaryName()))) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if (item == null || item.getItemMeta() == null) return;
            Player p = (Player) e.getWhoClicked();

            String name = item.getItemMeta().getDisplayName();
            ConfigManager c = RPGBestiary.configManager;
            if (name.equals(RPGBestiary.transformString(c.getNextPageItem().getName()))) {
                int page = RPGBestiary.bestiaryPage.get(p.getUniqueId()) + 1;

                BestiaryManager.openBestiary(p, page);
            }
            else if (name.equals(RPGBestiary.transformString(c.getPreviousPageItem().getName()))) {
                int page = RPGBestiary.bestiaryPage.get(p.getUniqueId()) - 1;

                BestiaryManager.openBestiary(p, page);
            }
        }
    }

    @EventHandler
    public void clearOnClosing(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals(RPGBestiary.transformString(RPGBestiary.configManager.getBestiaryName()))) {
            RPGBestiary.bestiaryPage.remove(e.getPlayer().getUniqueId());
        }
    }
}
