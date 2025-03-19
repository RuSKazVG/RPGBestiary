package me.ruskaz.rpgbestiary.main;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.ruskaz.rpgbestiary.api.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MythicEvents implements Listener {

    @EventHandler
    public void addInBestiary(MythicMobDeathEvent e) {
        if (!(e.getKiller() instanceof Player p)) return;

        String mythicMobId = e.getMob().getMobType();

        if (!RPGBestiary.configManager.getMythicMobsList().containsKey(mythicMobId)) return;

        String mobId = RPGBestiary.configManager.getMythicMobsList().get(mythicMobId);

        String searchId = p.getUniqueId().toString();
        String sqlSelect = "SELECT id, list FROM bestiary WHERE id = ?";
        String list = null;
        try (PreparedStatement pstmt = DatabaseManager.database.prepareStatement(sqlSelect)) {
            pstmt.setString(1, searchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    list = rs.getString("list");
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


        if (!list.contains(mobId)) {
            if (RPGBestiary.configManager.getBestiaryKillMessage() != null) {
                String bestiaryKillMessage = ChatColor.translateAlternateColorCodes('&', RPGBestiary.configManager.getBestiaryKillMessage().replaceAll("%mob_name%", e.getMob().getDisplayName()));
                p.sendMessage(bestiaryKillMessage);
            }

            DatabaseManager.addKilledMobToDatabase(p.getUniqueId(), mobId);
        }
    }
}
