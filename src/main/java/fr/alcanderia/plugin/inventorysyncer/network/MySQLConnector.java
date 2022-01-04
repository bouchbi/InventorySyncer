package fr.alcanderia.plugin.inventorysyncer.network;

import fr.alcanderia.plugin.inventorysyncer.*;
import org.bukkit.*;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class MySQLConnector {
	
	private static final Logger     logger      = InventorySyncer.getInstance().getLogger();
	static final         String     DB_url      = "jdbc:mysql://" + InventorySyncer.getConfiguration().getString("sqlCredentials.host") + ":" + InventorySyncer.getConfiguration().getString("sqlCredentials.port") + "/" + InventorySyncer.getConfiguration().getString("sqlCredentials.dbName") + "?serverTimezone=UTC";
	static final         String     DB_user     = InventorySyncer.getConfiguration().getString("sqlCredentials.user");
	static final         String     DB_password = InventorySyncer.getConfiguration().getString("sqlCredentials.password");
	static               String     tabName     = InventorySyncer.getConfiguration().getString("sqlCredentials.dbTableName");
	private static       Connection con;
	
	public static void createTable() {
		reopenIfClosed();
		
		if (tabName != null) {
			try {
				Statement stmt = con.createStatement();
				
				try {
					String sql = "CREATE TABLE " + tabName + "(id VARCHAR(40) not NULL,  inv VARCHAR(50000) DEFAULT NULL)";
					stmt.executeUpdate(sql);
					logger.info("Successfully created table in given database");
				}
				catch (SQLException e) {
					logger.warning("error creating table in database");
					e.printStackTrace();
				}
				finally {
					if (stmt != null) {
						try {
							stmt.close();
						}
						catch (SQLException e) {
							logger.warning("cannot close statement");
							e.printStackTrace();
						}
					}
					
				}
			}
			catch (SQLException e) {
				logger.warning("Error creating table in given database");
				e.printStackTrace();
			}
		}
		else {
			logger.warning("Cannot create table in given database, check for existing field 'dbTableName' in config");
		}
		
	}
	
	public static String getUserInv(UUID id) {
		reopenIfClosed();
		
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tabName);
			
			try {
				ResultSet rs = ps.executeQuery();
				
				try {
					String inv = null;
					
					String name;
					while (rs.next()) {
						name = rs.getString("id");
						if (!Objects.equals(name, id.toString())) {
							continue;
						}
						
						inv = rs.getString("inv");
					}
					
					if (inv == null) {
						logger.warning("Inventory of player " + id + "(" + Bukkit.getPlayer(id).getName() + ") is not saved in database");
					}
					
					return inv;
				}
				catch (SQLException e) {
					logger.warning("Cannot get player " + id + "(" + Bukkit.getPlayer(id).getName() + ") inventory in database");
					e.printStackTrace();
				}
				finally {
					if (rs != null) {
						try {
							rs.close();
						}
						catch (SQLException e) {
							logger.warning("cannot close statement");
							e.printStackTrace();
						}
					}
					
				}
			}
			catch (SQLException e) {
				logger.warning("cannot execute query");
				e.printStackTrace();
			}
			finally {
				if (ps != null) {
					try {
						ps.close();
					}
					catch (SQLException e) {
						logger.warning("cannot close statement");
						e.printStackTrace();
					}
				}
				
			}
		}
		catch (SQLException e) {
			logger.warning("Unable to read player " + id + "(" + Bukkit.getPlayer(id).getName() + ") inventory from database");
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public static boolean checkExistingInv(UUID id) {
		reopenIfClosed();
		
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tabName);
			
			try {
				ResultSet rs = ps.executeQuery();
				
				try {
					while(rs.next()) {
						if (!Objects.equals(rs.getString("id"), id.toString())) {
							continue;
						}
						return true;
					}
				}
				catch (SQLException e) {
					logger.warning("cannot check user inv existence for " + id + " (" + Bukkit.getPlayer(id).getName() + ")");
					e.printStackTrace();
					return false;
				}
				finally {
					if (rs != null) {
						try {
							rs.close();
						}
						catch (SQLException e) {
							logger.warning("cannot close statement");
							e.printStackTrace();
						}
					}
					
				}
			}
			catch (SQLException e) {
				logger.warning("cannot check user inv existence for " + id + " (" + Bukkit.getPlayer(id).getName() + ")");
				e.printStackTrace();
				return false;
			}
			finally {
				if (ps != null) {
					try {
						ps.close();
					}
					catch (SQLException e) {
						logger.warning("cannot close statement");
						e.printStackTrace();
					}
				}
			}
		}
		catch (SQLException e) {
			logger.warning("Unable to check if player " + id + "(" + Bukkit.getPlayer(id).getName() + ") inventory exists in table " + tabName + " in database");
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public static void writeUserInv(UUID id, String string) {
		reopenIfClosed();
		
		ResultSet rs          = null;
		int       candidateID = 0;
		String    sqlInsert   = "INSERT INTO " + tabName + "(id, inv)VALUES(?, ?)";
		String    sqlUpdate   = "UPDATE " + tabName + " SET inv = ? WHERE id = ?";
		
		try {
			PreparedStatement pst = con.prepareStatement(checkExistingInv(id) ? sqlUpdate : sqlInsert);
			
			try {
				if (checkExistingInv(id)) {
					pst.setString(1, string);
					pst.setString(2, id.toString());
				}
				else {
					pst.setString(1, id.toString());
					pst.setString(2, string);
				}
				
				pst.executeUpdate();
			}
			catch (SQLException e) {
				logger.warning("cannot write inventory for player " + id + " (" + Bukkit.getPlayer(id).getName() + ")");
				e.printStackTrace();
			}
			finally {
				if (pst != null) {
					try {
						pst.close();
					}
					catch (SQLException e) {
						logger.warning("cannot close statement");
						e.printStackTrace();
					}
				}
			}
		}
		catch (
		  SQLException e) {
			logger.warning("Error writing player " + id + " (" + Bukkit.getPlayer(id).getName() + ") to database");
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) {
					((ResultSet) rs).close();
				}
			}
			catch (SQLException e) {
				logger.warning("Error closing Statement");
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void reopenIfClosed() {
		try {
			if (con.isClosed()) {
				logger.info("connexion closed, trying to reopen to execute task");
				openConnexion();
			}
		} catch (SQLException e) {
			logger.warning("cannot check connexion status");
			e.printStackTrace();
		}
	}
	
	public static void initConnexion() {
		openConnexion();
		
		try {
			if (!checkTableExistence(tabName)) {
				logger.info("Table not found in database, will attempt to create one");
				createTable();
			}
		}
		catch (SQLException var1) {
			logger.warning("Unable to check table existence in database");
			var1.printStackTrace();
		}
		
	}
	
	public static void openConnexion() {
		try {
			con = DriverManager.getConnection(DB_url, DB_user, DB_password);
			logger.info("Successfully connected to the given database");
		}
		catch (SQLException e) {
			logger.warning("Error connecting to database, check the given information in the config");
			e.printStackTrace();
		}
	}
	
	public static void closeConnexion() {
		try {
			if (con != null) {
				con.close();
				logger.info("Successfully disconnected from database");
			}
		}
		catch (SQLException var1) {
			logger.warning("Unable to close connexion with database");
			var1.printStackTrace();
		}
		
	}
	
	private static boolean checkTableExistence(String tableName) throws SQLException {
		DatabaseMetaData dbmt = con.getMetaData();
		ResultSet        res  = dbmt.getTables((String) null, (String) null, tableName, new String[] { "TABLE" });
		return res.next();
	}
	
}
