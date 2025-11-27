package me.sosedik.utilizer.api.database;

import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.sql.Connection;
import java.sql.SQLException;

@NullMarked
public interface Database {

	/**
	 * Creates a database wrapper
	 *
	 * @param plugin plugin
	 * @param databaseName database name
	 * @return database wrapper
	 */
	static Database prepareDatabase(Plugin plugin, String databaseName) {
		if (plugin.getConfig().getBoolean("connection.use-mysql")) {
			try {
				return new MySQL(plugin.getConfig());
			} catch (Exception e) {
				plugin.getComponentLogger().error("Couldn't connect to the MySQL database! Using SQLite instead.");
			}
		}
		return new SQLite(plugin, plugin.getDataFolder(), databaseName);
	}

	/**
	 * Opens the database connection
	 *
	 * @return database connection
	 * @throws SQLException sql exception
	 */
	Connection openConnection() throws SQLException;

	/**
	 * Closes the database connection
	 */
	void close();

}
