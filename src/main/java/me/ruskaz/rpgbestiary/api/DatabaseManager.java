package me.ruskaz.rpgbestiary.api;

import me.ruskaz.rpgbestiary.main.RPGBestiary;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {

    public static Connection database;

    public static void openDatabase() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + RPGBestiary.plugin.getDataFolder() + "/database.db");
            if (conn != null) {
                String sqlCreateTable = "CREATE TABLE IF NOT EXISTS bestiary (" +
                        "id TEXT PRIMARY KEY," +
                        "list TEXT)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlCreateTable)) {
                    pstmt.executeUpdate();
                }
                database = conn;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeDatabase() {
        try {
            database.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addKilledMobToDatabase(UUID uuid, String mobID) {
        String list = getPlayerKilledLine(uuid) + mobID + ";";

        String sqlInsert = "INSERT INTO bestiary(id, list) VALUES(?, ?) " +
                "ON CONFLICT(id) DO UPDATE SET list=excluded.list";
        try (PreparedStatement pstmt = database.prepareStatement(sqlInsert)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, list);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void removeMobFromDatabase(UUID uuid, String mobID) {
        String list = getPlayerKilledLine(uuid);
        list = list.replaceAll(mobID + ";", "");

        String sqlInsert = "INSERT INTO bestiary(id, list) VALUES(?, ?) " +
                "ON CONFLICT(id) DO UPDATE SET list=excluded.list";
        try (PreparedStatement pstmt = database.prepareStatement(sqlInsert)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, list);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getPlayerKilledLine(UUID uuid) {
        String searchId = uuid.toString();
        String sqlSelect = "SELECT id, list FROM bestiary WHERE id = ?";
        String list = null;
        try (PreparedStatement pstmt = database.prepareStatement(sqlSelect)) {
            pstmt.setString(1, searchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    list = rs.getString("list");
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return list;
    }

    public static String[] getPlayerKilledList(UUID uuid) {
        return getPlayerKilledLine(uuid).split(";");
    }
}
