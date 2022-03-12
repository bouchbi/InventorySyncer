package fr.alcanderia.plugin.inventorysyncer.network;

import fr.alcanderia.plugin.inventorysyncer.*;
import org.bukkit.*;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.logging.*;

public class MySQLConnector {

    private static final Logger logger = InventorySyncer.getInstance().getLogger();
    static final String DB_url = "jdbc:mysql://" + InventorySyncer.getConfiguration().getString("sqlCredentials.host") + ":" + InventorySyncer.getConfiguration().getString("sqlCredentials.port") + "/" + InventorySyncer.getConfiguration().getString("sqlCredentials.dbName") + "?serverTimezone=UTC";
    static final String DB_user = InventorySyncer.getConfiguration().getString("sqlCredentials.user");
    static final String DB_password = InventorySyncer.getConfiguration().getString("sqlCredentials.password");
    static String invTabName = InventorySyncer.getConfiguration().getString("sqlCredentials.dbInvTableName");
    static String ecTabName = InventorySyncer.getConfiguration().getString("sqlCredentials.dbECTableName");
    private static Connection con;

    public static void createTable(String tabName) {
        if (tabName != null) {
            try {
                Statement stmt = con.createStatement();

                try {
                    String sql = "CREATE TABLE " + tabName + "(id VARCHAR(40) not NULL, name VARCHAR(25) DEFAULT NULL,  inv BLOB(50000) DEFAULT NULL, backup BLOB(50000) DEFAULT NULL)";
                    stmt.executeUpdate(sql);
                    logger.info("Successfully created " + tabName + " table in given database");
                } catch (SQLException e) {
                    logger.warning("error creating table " + tabName + " in database");
                    e.printStackTrace();
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                            logger.warning("cannot close statement");
                            e.printStackTrace();
                        }
                    }

                }
            } catch (SQLException e) {
                logger.warning("Error creating table in given database");
                e.printStackTrace();
            }
        } else {
            logger.warning("Cannot create table in given database, check for existing fields 'dbInvTableName' and 'sbECTableName' in config");
        }
    }

    public static String getUserInv(UUID id, String tabName, boolean backup) {

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tabName);

            try {
                ResultSet rs = ps.executeQuery();

                try {
                    Blob blob = null;
                    String inv = null;

                    String name;
                    while (rs.next()) {
                        name = rs.getString("id");
                        if (!Objects.equals(name, id.toString())) {
                            continue;
                        }

                        blob = rs.getBlob(backup ? "backup" : "inv");
                    }

                    if (blob == null) {
                        logger.warning("Ec Inventory of player " + id + "(" + Bukkit.getPlayer(id).getName() + ") is not saved in database");
                    } else {
                        byte[] data = blob.getBytes(1, (int) blob.length());
                        inv = new String(data);
                    }

                    return inv;
                } catch (SQLException e) {
                    logger.warning("Cannot get player " + id + "(" + Bukkit.getPlayer(id).getName() + ") ec inventory in database");
                    e.printStackTrace();
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            logger.warning("cannot close statement");
                            e.printStackTrace();
                        }
                    }

                }
            } catch (SQLException e) {
                logger.warning("cannot execute query");
                e.printStackTrace();
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        logger.warning("cannot close statement");
                        e.printStackTrace();
                    }
                }

            }
        } catch (SQLException e) {
            logger.warning("Unable to read player " + id + "(" + Bukkit.getPlayer(id).getName() + ") ec inventory from database");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static boolean checkExistingInv(UUID id, String tabName) {

        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tabName);

            try {
                ResultSet rs = ps.executeQuery();

                try {
                    while (rs.next()) {
                        if (!Objects.equals(rs.getString("id"), id.toString())) {
                            continue;
                        }
                        return true;
                    }
                } catch (SQLException e) {
                    logger.warning("cannot check user inv existence for " + id + " (" + Bukkit.getPlayer(id).getName() + ") in table" + tabName);
                    e.printStackTrace();
                    return false;
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            logger.warning("cannot close statement");
                            e.printStackTrace();
                        }
                    }

                }
            } catch (SQLException e) {
                logger.warning("cannot check user inv existence for " + id + " (" + Bukkit.getPlayer(id).getName() + ") in table" + tabName);
                e.printStackTrace();
                return false;
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        logger.warning("cannot close statement");
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            logger.warning("Unable to check if player " + id + "(" + Bukkit.getPlayer(id).getName() + ") inventory exists in table " + tabName + " in database");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void writeInv(UUID id, String inv, String tabName, boolean backup) {

        ResultSet rs = null;
        String sqlInsert = "INSERT INTO " + tabName + "(id, " + (backup ? "backup" : "inv") + ")VALUES(?, ?)";
        String sqlUpdate = "UPDATE " + tabName + " SET " + (backup ? "backup" : "inv") + "= ? WHERE id = ?";

        try {
            PreparedStatement pst = con.prepareStatement(checkExistingInv(id, tabName) ? sqlUpdate : sqlInsert);

            try {
                byte[] data = inv.getBytes(StandardCharsets.UTF_8);
                Blob blob = con.createBlob();
                blob.setBytes(1, data);

                if (checkExistingInv(id, tabName)) {
                    pst.setBlob(1, blob);
                    pst.setString(2, id.toString());
                } else {
                    pst.setString(1, id.toString());
                    pst.setBlob(2, blob);
                }

                pst.executeUpdate();
            } catch (SQLException e) {
                logger.warning("cannot write inventory for player " + id + " (" + Bukkit.getPlayer(id).getName() + ")");
                e.printStackTrace();
            } finally {
                if (pst != null) {
                    try {
                        pst.close();
                    } catch (SQLException e) {
                        logger.warning("cannot close statement");
                        e.printStackTrace();
                    }
                }
            }
        } catch (
                SQLException e) {
            logger.warning("Error writing inventory for " + id + " (" + Bukkit.getPlayer(id).getName() + ") to database");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    ((ResultSet) rs).close();
                }
            } catch (SQLException e) {
                logger.warning("Error closing Statement");
                e.printStackTrace();
            }

        }

    }

    public static void initConnexion() {
        Connection con = openConnexion();

        try {
            con.setNetworkTimeout(command -> {
                logger.warning("Connexion timed out, closing and reopening it");
                MySQLConnector.closeConnexion();
                MySQLConnector.openConnexion();
            }, 10000);
        } catch (SQLException e) {
            logger.warning("Cannot set timeout");
            e.printStackTrace();
        }

        try {
            if (!checkTableExistence(invTabName)) {
                logger.info("Table " + invTabName + " not found in database, will attempt to create one");
                createTable(invTabName);
            }
            if (!checkTableExistence(ecTabName)) {
                logger.info("Table " + ecTabName + " not found in database, will attempt to create one");
                createTable(ecTabName);
            }
        } catch (SQLException var1) {
            logger.warning("Unable to check table existence in database");
            var1.printStackTrace();
        }

    }

    public static Connection openConnexion() {
        try {
            con = DriverManager.getConnection(DB_url, DB_user, DB_password);
            logger.info("Successfully connected to the given database");
        } catch (SQLException e) {
            logger.warning("Error connecting to database, check the given information in the config");
            e.printStackTrace();
        }

        return con;
    }

    public static void closeConnexion() {
        try {
            if (con != null) {
                con.close();
                logger.info("Successfully disconnected from database");
            }
        } catch (SQLException var1) {
            logger.warning("Unable to close connexion with database");
            var1.printStackTrace();
        }

    }

    private static boolean checkTableExistence(String tableName) throws SQLException {
        DatabaseMetaData dbmt = con.getMetaData();
        ResultSet res = dbmt.getTables((String) null, (String) null, tableName, new String[]{"TABLE"});
        return res.next();
    }

}
