package me.sosedik.utilizer.api.database;

import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NullMarked
public class SQLite implements Database {

	private final File dataFolder;

	public SQLite(Plugin plugin, File folder, String databaseName) {
		dataFolder = new File(folder, databaseName + ".db");
		if (!dataFolder.exists()) {
			try {
				if (!dataFolder.createNewFile())
					plugin.getComponentLogger().error("Could not create a database file!");
			} catch (IOException e) {
				plugin.getComponentLogger().error("File write error: {}.db", databaseName);
			}
		}
	}

	@Override
	public Connection openConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
	}

	@Override
	public void close() {
		// We have nothing to close
	}

}
