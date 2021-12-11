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
		if (tabName != null) {
			try {
				Statement stmt = con.createStatement();
				Throwable var1 = null;
				
				try {
					String sql = "CREATE TABLE " + tabName + "(id VARCHAR(40) not NULL,  inv VARCHAR(50000) DEFAULT NULL)";
					stmt.executeUpdate(sql);
					logger.info("Successfully created table in given database");
				} catch (Throwable var11) {
					var1 = var11;
					throw var11;
				} finally {
					if (stmt != null) {
						if (var1 != null) {
							try {
								stmt.close();
							} catch (Throwable var10) {
								var1.addSuppressed(var10);
							}
						} else {
							stmt.close();
						}
					}
					
				}
			} catch (SQLException var13) {
				logger.warning("Error creating table in given database");
				var13.printStackTrace();
			}
		} else {
			logger.warning("Cannot create table in given database, check for existing field 'dbTableName' in config");
		}
		
	}
	
	public static String getUserInv(UUID id) {
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tabName);
			Throwable var2 = null;
			
			try {
				ResultSet rs = ps.executeQuery();
				Throwable var4 = null;
				
				try {
					String inv = null;
					
					while(true) {
						String name;
						if (rs.next()) {
							name = rs.getString("id");
							if (!Objects.equals(name, id.toString())) {
								continue;
							}
							
							inv = rs.getString("inv");
						}
						
						if (inv == null) {
							logger.warning("Inventory of player " + id + "(" + Bukkit.getPlayer(id).getName() + ") is not saved in database");
						}
						
						name = inv;
						return name;
					}
				} catch (Throwable var31) {
					var4 = var31;
					throw var31;
				} finally {
					if (rs != null) {
						if (var4 != null) {
							try {
								rs.close();
							} catch (Throwable var30) {
								var4.addSuppressed(var30);
							}
						} else {
							rs.close();
						}
					}
					
				}
			} catch (Throwable var33) {
				var2 = var33;
				throw var33;
			} finally {
				if (ps != null) {
					if (var2 != null) {
						try {
							ps.close();
						} catch (Throwable var29) {
							var2.addSuppressed(var29);
						}
					} else {
						ps.close();
					}
				}
				
			}
		} catch (SQLException var35) {
			logger.warning("Unable to read player " + id + "(" + Bukkit.getPlayer(id).getName() + ") inventory from database");
			var35.printStackTrace();
			return null;
		}
	}
	
	public static boolean checkExistingInv(UUID id) {
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tabName);
			Throwable var2 = null;
			
			boolean var6;
			try {
				ResultSet rs = ps.executeQuery();
				Throwable var4 = null;
				
				try {
					if (!rs.next()) {
						return false;
					}
					
					String name = rs.getString("id");
					var6 = Objects.equals(name, id.toString());
				} catch (Throwable var34) {
					var4 = var34;
					throw var34;
				} finally {
					if (rs != null) {
						if (var4 != null) {
							try {
								rs.close();
							} catch (Throwable var33) {
								var4.addSuppressed(var33);
							}
						} else {
							rs.close();
						}
					}
					
				}
			} catch (Throwable var36) {
				var2 = var36;
				throw var36;
			} finally {
				if (ps != null) {
					if (var2 != null) {
						try {
							ps.close();
						} catch (Throwable var32) {
							var2.addSuppressed(var32);
						}
					} else {
						ps.close();
					}
				}
				
			}
			
			return var6;
		} catch (SQLException var38) {
			logger.warning("Unable to check if player " + id + "(" + Bukkit.getPlayer(id).getName() + ") inventory exists in table " + tabName + " in database");
			var38.printStackTrace();
			return false;
		}
	}
	
	public static void writeUserInv(UUID id, String string) {
		ResultSet rs = null;
		int candidateID = 0;
		String sqlInsert = "INSERT INTO " + tabName + "(id, inv)VALUES(?, ?)";
		String sqlUpdate = "UPDATE " + tabName + " SET inv = ? WHERE id = ?";
		
		try {
			PreparedStatement pst = con.prepareStatement(checkExistingInv(id) ? sqlUpdate : sqlInsert);
			Throwable var7 = null;
			
			try {
				if (checkExistingInv(id)) {
					pst.setString(1, string);
					pst.setString(2, id.toString());
				} else {
					pst.setString(1, id.toString());
					pst.setString(2, string);
				}
				
				pst.executeUpdate();
			} catch (Throwable var31) {
				var7 = var31;
				throw var31;
			} finally {
				if (pst != null) {
					if (var7 != null) {
						try {
							pst.close();
						} catch (Throwable var30) {
							var7.addSuppressed(var30);
						}
					} else {
						pst.close();
					}
				}
				
			}
		} catch (SQLException var33) {
			logger.warning("Error writing player " + id + " (" + Bukkit.getPlayer(id).getName() + ") to database");
			var33.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					((ResultSet)rs).close();
				}
			} catch (SQLException var29) {
				logger.warning("Error closing Statement");
				var29.printStackTrace();
			}
			
		}
		
	}
	
	public static void initConnexion() {
		try {
			con = DriverManager.getConnection(DB_url, DB_user, DB_password);
			logger.info("Successfully connected to the given database");
		} catch (SQLException var2) {
			logger.warning("Error connecting to database, check the given information in the config");
			var2.printStackTrace();
		}
		
		try {
			if (!checkTableExistence(tabName)) {
				logger.info("Table not found in database, will attempt to create one");
				createTable();
			}
		} catch (SQLException var1) {
			logger.warning("Unable to check table existence in database");
			var1.printStackTrace();
		}
		
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
		ResultSet res = dbmt.getTables((String)null, (String)null, tableName, new String[]{"TABLE"});
		return res.next();
	}
}
