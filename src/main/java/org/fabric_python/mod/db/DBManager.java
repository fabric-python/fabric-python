package org.fabric_python.mod.db;

import net.minecraft.client.MinecraftClient;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.List;

public class DBManager {
    Connection c;

    public void loadDatabase(MinecraftClient client) throws SQLException {
        String runDirectoryAbsolutePath = client.runDirectory.getAbsolutePath();
        String connection = "jdbc:sqlite:" + runDirectoryAbsolutePath + "/chests.db";

        c = DriverManager.getConnection(connection);

        DatabaseMetaData meta = c.getMetaData();

        ResultSet res = meta.getTables(null, null, "Chests",
                new String[]{"TABLE"});

        if (!res.next()) {
            PreparedStatement stmt = c.prepareStatement(
                    "CREATE TABLE \"ChestHashes\" (\n" +
                            "\t\"groupName\"\tTEXT NOT NULL,\n" +
                            "\t\"x\"\tINTEGER NOT NULL,\n" +
                            "\t\"y\"\tINTEGER NOT NULL,\n" +
                            "\t\"z\"\tINTEGER NOT NULL,\n" +
                            "\t\"hash\"\tTEXT NOT NULL,\n" +
                            "\tPRIMARY KEY(\"groupName\",\"x\",\"y\",\"z\")\n" +
                            ")");
            stmt.execute();

            stmt = c.prepareStatement("CREATE TABLE \"Chests\" (\n" +
                    "\t\"groupName\"\tTEXT NOT NULL,\n" +
                    "\t\"x\"\tINTEGER NOT NULL,\n" +
                    "\t\"y\"\tINTEGER NOT NULL,\n" +
                    "\t\"z\"\tINTEGER NOT NULL,\n" +
                    "\t\"slot\"\tINTEGER NOT NULL,\n" +
                    "\t\"itemName\"\tTEXT NOT NULL,\n" +
                    "\t\"num\"\tNUMERIC NOT NULL,\n" +
                    "\t\"tags\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"groupName\",\"x\",\"y\",\"z\",\"slot\")\n" +
                    ")");
            stmt.execute();

            stmt = c.prepareStatement("CREATE INDEX \"itemName\" ON \"Chests\" (\n" +
                    "\t\"itemName\"\n" +
                    ")");

            stmt.execute();
        }
    }

    public Boolean checkChestUnchanged(String groupName, int x, int y, int z, List<ChestEntry> list) throws SQLException {
        /* obtain the hash value */
        PreparedStatement stmt = c.prepareStatement("SELECT hash from \"ChestHashes\" where groupName = ? and x = ? and y = ? and z = ?");
        stmt.setString(1, groupName);
        stmt.setInt(2, x);
        stmt.setInt(3, y);
        stmt.setInt(4, z);

        ResultSet resultSet = stmt.executeQuery();

        if (!resultSet.next()) {
            return true;
        }else{
            String hash_in_the_database = resultSet.getString("hash");
            String hash_of_the_list = computeHash(list);

            return hash_in_the_database.equals(hash_of_the_list);
        }
    }

    public String computeHash(List<ChestEntry> list) {
        StringBuilder builder = new StringBuilder();
        for(ChestEntry entry: list) {
            if(entry.tags == null){
                builder.append(entry.itemName).append("\t").append(entry.num).append("\n");
            }else{
                builder.append(entry.itemName).append("\t").append(entry.num).append("\t").append(entry.tags.toString()).append("\n");
            }
        }

        String data = builder.toString();

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        md.update(data.getBytes());

        byte[] digest = md.digest();
        return String.format("%064x", new BigInteger(1, digest));
    }

    public void updateChest(String groupName, int x, int y, int z, List<ChestEntry> list) throws SQLException {
        /* delete all previous records on this chest */
        PreparedStatement stmt = c.prepareStatement("DELETE from \"Chests\" WHERE groupName = ? and x = ? and y =? and z = ?");
        stmt.setString(1, groupName);
        stmt.setInt(2, x);
        stmt.setInt(3, y);
        stmt.setInt(4, z);
        stmt.execute();

        /* insert new records */
        stmt = c.prepareStatement("INSERT INTO \"Chests\" (groupName, x, y, z, slot, itemName, num, tags) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        stmt.setString(1, groupName);
        stmt.setInt(2, x);
        stmt.setInt(3, y);
        stmt.setInt(4, z);
        for (ChestEntry entry : list) {
            stmt.setInt(5, entry.slot);
            stmt.setString(6, entry.itemName);
            stmt.setInt(7, entry.num);
            if(entry.tags != null){
                stmt.setString(8, entry.tags.toString());
            }else{
                stmt.setString(8, "");
            }
            stmt.execute();
        }

        /* update the hash */
        String hash_of_the_list = computeHash(list);
        updateHash(groupName, x, y, z, hash_of_the_list);
    }

    public void updateHash(String groupName, int x, int y, int z, String hash) throws SQLException{
        PreparedStatement stmt = c.prepareStatement("REPLACE INTO \"ChestHashes\" (groupName, x, y, z, hash) VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, groupName);
        stmt.setInt(2, x);
        stmt.setInt(3, y);
        stmt.setInt(4, z);
        stmt.setString(5, hash);
        stmt.execute();
    }

    public void initHash(String groupName, int x, int y, int z, String hash) throws SQLException{
        PreparedStatement stmt = c.prepareStatement("INSERT OR IGNORE INTO \"ChestHashes\" (groupName, x, y, z, hash) VALUES (?, ?, ?, ?, ?)");
        stmt.setString(1, groupName);
        stmt.setInt(2, x);
        stmt.setInt(3, y);
        stmt.setInt(4, z);
        stmt.setString(5, hash);
        stmt.execute();
    }

    public void close() throws SQLException {
        c.close();
    }
}
