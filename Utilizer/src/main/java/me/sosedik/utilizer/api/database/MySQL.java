package me.sosedik.utilizer.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.jspecify.annotations.NullMarked;

import java.sql.Connection;
import java.sql.SQLException;

@NullMarked
public class MySQL implements Database {

	private final HikariDataSource dataSource;
	private final String host;
	private final String database;
	private final String username;
	private final String password;
	private final int port;

	public MySQL(FileConfiguration config) {
		host = config.getString("connection.mysql.host");
		port = config.getInt("connection.mysql.port");
		database = config.getString("connection.mysql.database");
		username = config.getString("connection.mysql.username");
		password = config.getString("connection.mysql.password");

		this.dataSource = setupPool();
	}

	private HikariDataSource setupPool() {
		var config = new HikariConfig();
		try {
			config.setDriverClassName(Class.forName("com.mysql.cj.jdbc.Driver").getName());
		} catch (ClassNotFoundException e) {
			config.setDriverClassName("com.mysql.jdbc.Driver");
		}
		config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false");
		config.setUsername(username);
		config.setPassword(password);
		return new HikariDataSource(config);
	}

	@Override
	public Connection openConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void close() {
		if (!dataSource.isClosed())
			dataSource.close();
	}

}
