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

	private final File databaseFile;

	public SQLite(Plugin plugin, File folder, String databaseName) {
		this.databaseFile = new File(folder, databaseName + ".db");

		createDatabase(plugin);
	}

	private void createDatabase(Plugin plugin) {
		if (this.databaseFile.exists()) return;

		try {
			if (!this.databaseFile.createNewFile())
				plugin.getComponentLogger().error("Could not create a database file!");
		} catch (IOException e) {
			plugin.getComponentLogger().error("File write error: {}.db", this.databaseFile.getName(), e);
		}
	}

	@Override
	public Connection openConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + this.databaseFile);
	}

	@Override
	public void close() {
		// We have nothing to close
	}

}
