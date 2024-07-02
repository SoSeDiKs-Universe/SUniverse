package me.sosedik.fancymotd;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.database.Database;
import me.sosedik.utilizer.api.language.LangHolder;
import me.sosedik.utilizer.api.language.LangOptions;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Represents a client that pings the server
 */
public class Pinger {

	public static final String DATABASE_NAME = "Pingers";
	private static final Database PINGERS_DATABASE = Database.prepareDatabase(FancyMotd.instance(), DATABASE_NAME.toLowerCase());

	private static final LoadingCache<String, Pinger> PINGERS = CacheBuilder.newBuilder()
			.expireAfterAccess(5, TimeUnit.MINUTES)
			.build(
				new CacheLoader<>() {
					public @NotNull Pinger load(@NotNull String ip) {
						return constructPinger(ip);
					}
				}
			);

	private static @Nullable Predicate<UUID> clockAccessor = null;

	private final LangOptions langOptions;
	private final boolean hasClock;
	private final boolean isNewbie;

	private Pinger(@NotNull String ip, boolean hasClock) {
		this(LangOptionsStorage.getByAddress(ip), hasClock, true);
	}

	private Pinger(@NotNull UUID uuid) {
		this(LangHolder.langHolder(uuid).getLangOptions(), clockAccessor != null && clockAccessor.test(uuid), false);
	}

	private Pinger(@NotNull LangOptions langOptions, boolean hasClock) {
		this(langOptions, hasClock, false);
	}

	private Pinger(@NotNull LangOptions langOptions, boolean hasClock, boolean isNewbie) {
		this.langOptions = langOptions;
		this.hasClock = hasClock;
		this.isNewbie = isNewbie;
	}

	/**
	 * Gets the language used by the client
	 *
	 * @return supported language key for messages
	 */
	public @NotNull LangOptions getLanguage() {
		return langOptions;
	}

	/**
	 * Checks if this pinger has a clock in the equipment
	 *
	 * @return true, if any player with this IP has a clock
	 */
	public boolean hasClock() {
		return hasClock;
	}

	/**
	 * Checks if this user wasn't recognized as already played
	 *
	 * @return true, if player haven't joined the server before
	 */
	public boolean isNewbie() {
		return isNewbie;
	}

	/**
	 * Creates a Pinger instance from the provided IP
	 *
	 * @param ip client's IP address
	 * @return Pinger instance
	 */
	private static @NotNull Pinger constructPinger(@NotNull String ip) {
		try (var con = PINGERS_DATABASE.openConnection();
			 var ps = con.prepareStatement("SELECT * FROM " + DATABASE_NAME + " WHERE IP LIKE '%" + ip + "%'")) {
			ResultSet rs = ps.executeQuery();
			boolean hasClock = clockAccessor == null;
			LangOptions language = null;
			while (rs.next()) {
				language = LangOptionsStorage.getLangOptionsIfExist(rs.getString("LastLang"));
				if (rs.getBoolean("Clock")) {
					hasClock = true;
					break;
				}
			}
			return language == null ? new Pinger(ip, hasClock) : new Pinger(language, hasClock);
		} catch (SQLTransientConnectionException ignored) {
			FancyMotd.logger().error("Could not connect to the database!");
		} catch (SQLException ex) {
			FancyMotd.logger().error("Could not connect to the database!", ex);
		}
		return new Pinger(ip, clockAccessor == null);
	}

	/**
	 * Stores a Pinger instance from the provided Player object
	 *
	 * @param player player
	 */
	public static void addPinger(@NotNull Player player) {
		InetSocketAddress address = player.getAddress();
		if (address == null) return;

		UUID uuid = player.getUniqueId();
		String ip = address.getAddress().getHostAddress();
		Pinger pinger = new Pinger(uuid);
		PINGERS.put(ip, pinger);

		Utilizer.scheduler().async(() -> updatePingerData(pinger, uuid, ip));
	}

	/**
	 * Removes a Pinger instance from memory
	 *
	 * @param ip IP
	 */
	public static void removePinger(@NotNull String ip) {
		PINGERS.invalidate(ip);
	}

	private static void updatePingerData(@NotNull Pinger pinger, @NotNull UUID uuid, @NotNull String ip) {
		String getIpsSql = "SELECT IP FROM " + DATABASE_NAME + " WHERE UUID = '" + uuid + "';";
		String updateDataSql = "INSERT OR REPLACE INTO " + DATABASE_NAME + "(UUID, IP, LastLang, Clock) VALUES(?, ?, ?, ?)";
		try (Connection con = PINGERS_DATABASE.openConnection();
			 PreparedStatement ps = con.prepareStatement(getIpsSql)) {
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String[] oldIps = rs.getString("IP").split("\\|");
				boolean updateIps = true;
				for (String oldIp : oldIps) {
					if (ip.equals(oldIp)) {
						updateIps = false;
						break;
					}
				}
				if (updateIps) {
					ip = switch (oldIps.length) {
						case 1 -> oldIps[0] + "|" + ip;
						case 2 -> oldIps[0] + "|" + oldIps[1] + "|" + ip;
						default -> oldIps[1] + "|" + oldIps[2] + "|" + ip;
					};
				}
			}
			try (PreparedStatement ps1 = con.prepareStatement(updateDataSql)) {
				ps1.setString(1, uuid.toString());
				ps1.setString(2, ip);
				ps1.setString(3, pinger.getLanguage().minecraftId());
				ps1.setBoolean(4, clockAccessor != null && pinger.hasClock());
				ps1.executeUpdate();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Returns a Pinger instance for the provided IP.
	 * <br>Will create a Pinger instance if missing.
	 *
	 * @param ip IP
	 * @return Pinger instance
	 */
	public static @NotNull Pinger getPinger(@NotNull String ip) {
		return PINGERS.getUnchecked(ip);
	}

	/**
	 * Sets the check for displaying a clock
	 *
	 * @param clockAccessor clock check
	 */
	@SuppressWarnings("unused")
	public static void setClockAccessor(@NotNull Predicate<UUID> clockAccessor) {
		Pinger.clockAccessor = clockAccessor;
	}

	static void setupDatabase() {
		String createTableSql = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + "(`UUID` varchar(64) NOT NULL, `IP` varchar(64), `LastLang` varchar(64), 'Clock' BIT(1), PRIMARY KEY(`UUID`));";
		try (Connection con = PINGERS_DATABASE.openConnection();
		     PreparedStatement ps = con.prepareStatement(createTableSql)) {
			ps.executeUpdate();
		} catch (SQLException e) {
			FancyMotd.logger().error("Could not create pingers database!", e);
			Bukkit.getPluginManager().disablePlugin(FancyMotd.instance());
		}
	}

	static void closeDatabase() {
		PINGERS_DATABASE.close();
	}

	static void runCleanupTask() {
		long cleanupInterval = 60 * 20L;
		Utilizer.scheduler().async(PINGERS::cleanUp, cleanupInterval, cleanupInterval);
	}

}
